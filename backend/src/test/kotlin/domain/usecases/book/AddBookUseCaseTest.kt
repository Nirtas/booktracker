package domain.usecases.book

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator
import ru.jerael.booktracker.backend.domain.usecases.book.AddBookUseCase
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class AddBookUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var genresValidator: GenresValidator

    @MockK
    private lateinit var coverStorage: CoverStorage

    private lateinit var useCase: AddBookUseCase

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val foundGenres = listOf(
        Genre(1, "genre 1"),
        Genre(2, "genre 2"),
        Genre(3, "genre 3")
    )
    private val expectedBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    private fun createPayload(genreIds: List<Int> = emptyList()) = BookCreationPayload(
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        genreIds = genreIds
    )

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val coverFileName: String = "cover.jpg"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = AddBookUseCase(bookRepository, genresValidator, coverStorage)
    }

    @Test
    fun `when genre validation is passed and the cover is present, the book must be successfully created with the cover`() =
        runTest {
            val requestedGenreIds = listOf(1, 2, 3)
            val bookCreationPayload = createPayload(requestedGenreIds)
            val expectedBookWithGenres = expectedBook.copy(genres = foundGenres)
            val expectedCoverPath = "covers/new_book.jpg"
            coEvery { genresValidator.invoke(requestedGenreIds, language) } just Runs
            coEvery { coverStorage.save(coverBytes, coverFileName) } returns expectedCoverPath
            coEvery { bookRepository.addBook(any(), language) } returns expectedBookWithGenres

            val result = useCase.invoke(bookCreationPayload, coverBytes, coverFileName, language)

            assertEquals(expectedBookWithGenres, result)
            coVerify(exactly = 1) { coverStorage.save(coverBytes, coverFileName) }
            coVerify(exactly = 1) { bookRepository.addBook(any(), language) }
        }

    @Test
    fun `when genre validation is passed and the cover is not present, the book must be successfully created without cover`() =
        runTest {
            val requestedGenreIds = listOf(1, 2, 3)
            val bookCreationPayload = createPayload(requestedGenreIds)
            val expectedBookWithGenres = expectedBook.copy(genres = foundGenres)
            coEvery { genresValidator.invoke(requestedGenreIds, language) } just Runs
            coEvery { bookRepository.addBook(bookCreationPayload, language) } returns expectedBookWithGenres

            val result = useCase.invoke(bookCreationPayload, null, null, language)

            assertEquals(expectedBookWithGenres, result)
            coVerify(exactly = 0) { coverStorage.save(any(), any()) }
            coVerify(exactly = 1) { bookRepository.addBook(any(), language) }
        }

    @Test
    fun `when genre validation is failed, a ValidationException should be thrown`() = runTest {
        val requestedGenreIds = listOf(1, 2, 3)
        val bookCreationPayload = createPayload(requestedGenreIds)
        coEvery { genresValidator.invoke(requestedGenreIds, language) } throws ValidationException("Error")

        assertThrows<ValidationException> {
            useCase.invoke(bookCreationPayload, coverBytes, coverFileName, language)
        }

        coVerify(exactly = 0) { coverStorage.save(any(), any()) }
        coVerify(exactly = 0) { bookRepository.addBook(any(), any()) }
    }
}