package domain.usecases.book

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator
import ru.jerael.booktracker.backend.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.backend.domain.usecases.book.UpdateBookDetailsUseCase
import java.time.Instant
import java.util.*

class UpdateBookDetailsUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var genresValidator: GenresValidator

    @RelaxedMockK
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase

    private lateinit var useCase: UpdateBookDetailsUseCase

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val foundGenres = listOf(
        Genre(1, "genre 1"),
        Genre(2, "genre 2"),
        Genre(3, "genre 3")
    )
    private val existingBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    private fun createPayload(genreIds: List<Int> = emptyList()) = BookDetailsUpdatePayload(
        title = "Title",
        author = "Author",
        status = BookStatus.READ,
        genreIds = genreIds
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateBookDetailsUseCase(bookRepository, genresValidator, getBookByIdUseCase)
    }

    @Test
    fun `when genre validation is passed, the book must be successfully updated`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookDetailsUpdatePayload = createPayload(requestedGenreIds)
        val updatedBook = existingBook.copy(genres = foundGenres)
        coEvery { genresValidator.invoke(requestedGenreIds, language) } just Runs
        coEvery { bookRepository.updateBookDetails(bookId, bookDetailsUpdatePayload, language) } returns updatedBook

        val result = useCase.invoke(bookId, bookDetailsUpdatePayload, language)

        assertEquals(updatedBook, result)
        coVerify(exactly = 1) { bookRepository.updateBookDetails(bookId, bookDetailsUpdatePayload, language) }
    }

    @Test
    fun `when genre validation is failed, a ValidationException should be thrown`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookDetailsUpdatePayload = createPayload(requestedGenreIds)
        coEvery { genresValidator.invoke(requestedGenreIds, language) } throws ValidationException("Error")

        assertThrows<ValidationException> {
            useCase.invoke(bookId, bookDetailsUpdatePayload, language)
        }

        coVerify(exactly = 0) { bookRepository.updateBookDetails(any(), any(), any()) }
    }

    @Test
    fun `when a book is not found, a BookNotFoundException should be thrown`() = runTest {
        val bookDetailsUpdatePayload = createPayload()
        coEvery { getBookByIdUseCase.invoke(bookId, language) } throws BookNotFoundException(bookId.toString())

        assertThrows<BookNotFoundException> {
            useCase.invoke(bookId, bookDetailsUpdatePayload, language)
        }

        coVerify(exactly = 0) { bookRepository.updateBookDetails(any(), any(), any()) }
    }

    @Test
    fun `when repository fails to update book, it should propagate the exception`() = runTest {
        val bookDetailsUpdatePayload = createPayload()
        coEvery { genresValidator.invoke(any(), any()) } just Runs
        coEvery {
            bookRepository.updateBookDetails(
                bookId,
                bookDetailsUpdatePayload,
                language
            )
        } throws BookNotFoundException(bookId.toString())

        val exception = assertThrows<BookNotFoundException> {
            useCase.invoke(bookId, bookDetailsUpdatePayload, language)
        }

        assertTrue(exception.message!!.contains("$bookId"))
    }
}