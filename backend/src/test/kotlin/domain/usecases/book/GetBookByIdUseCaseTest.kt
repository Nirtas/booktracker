package domain.usecases.book

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.book.GetBookByIdUseCase
import java.time.Instant
import java.util.*

class GetBookByIdUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    private lateinit var useCase: GetBookByIdUseCase

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val expectedBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetBookByIdUseCase(bookRepository)
    }

    @Test
    fun `when a book is found, the book is successfully returned`() = runTest {
        coEvery { bookRepository.getBookById(bookId, language) } returns expectedBook

        val result = useCase.invoke(bookId, language)

        assertEquals(expectedBook, result)
    }

    @Test
    fun `when a book is not found, a BookNotFoundException should be thrown`() = runTest {
        coEvery { bookRepository.getBookById(bookId, language) } returns null

        val exception = assertThrows<BookNotFoundException> {
            useCase.invoke(bookId, language)
        }

        assertTrue(exception.message!!.contains("$bookId"))
    }
}