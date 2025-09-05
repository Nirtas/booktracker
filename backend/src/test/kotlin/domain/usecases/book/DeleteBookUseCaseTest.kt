package domain.usecases.book

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ExternalServiceException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.book.DeleteBookUseCase
import ru.jerael.booktracker.backend.domain.usecases.book.GetBookByIdUseCase
import java.time.Instant
import java.util.*

class DeleteBookUseCaseTest {

    @RelaxedMockK
    private lateinit var bookRepository: BookRepository

    @RelaxedMockK
    private lateinit var fileStorage: FileStorage

    @MockK
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase

    private lateinit var useCase: DeleteBookUseCase

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val coverPath = "covers/book.jpg"
    private val bookWithCover = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = coverPath,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val bookWithoutCover = bookWithCover.copy(coverPath = null)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = DeleteBookUseCase(bookRepository, fileStorage, getBookByIdUseCase)
    }

    @Test
    fun `when the book and cover have been successfully removed`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns bookWithCover
        coEvery { bookRepository.deleteBook(bookId) } returns true

        useCase.invoke(bookId)

        coVerify(exactly = 1) { getBookByIdUseCase.invoke(bookId, language) }
        coVerify(exactly = 1) { fileStorage.deleteFile(coverPath) }
        coVerify(exactly = 1) { bookRepository.deleteBook(bookId) }
    }

    @Test
    fun `when a book has no cover, only the book details are successfully removed`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns bookWithoutCover
        coEvery { bookRepository.deleteBook(bookId) } returns true

        useCase.invoke(bookId)

        coVerify(exactly = 1) { getBookByIdUseCase.invoke(bookId, language) }
        coVerify(exactly = 0) { fileStorage.deleteFile(coverPath) }
        coVerify(exactly = 1) { bookRepository.deleteBook(bookId) }
    }

    @Test
    fun `when it is not possible to remove a cover from storage, a StorageException should be thrown`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns bookWithCover
        coEvery { fileStorage.deleteFile(any()) } throws Exception("Error")

        assertThrows<StorageException> {
            useCase.invoke(bookId)
        }

        coVerify(exactly = 0) { bookRepository.deleteBook(bookId) }
    }

    @Test
    fun `when it is not possible to delete book details from the database, an ExternalServiceException should be thrown`() =
        runTest {
            coEvery { getBookByIdUseCase.invoke(bookId, language) } returns bookWithCover
            coEvery { bookRepository.deleteBook(any()) } throws Exception("Error")

            assertThrows<ExternalServiceException> {
                useCase.invoke(bookId)
            }

            coVerify(exactly = 1) { fileStorage.deleteFile(coverPath) }
        }
}