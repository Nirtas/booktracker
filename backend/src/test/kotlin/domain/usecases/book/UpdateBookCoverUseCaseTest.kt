package domain.usecases.book

import io.ktor.http.content.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.backend.domain.usecases.book.UpdateBookCoverUseCase
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class UpdateBookCoverUseCaseTest {

    @MockK
    private lateinit var bookRepository: BookRepository

    @MockK
    private lateinit var coverStorage: CoverStorage

    @MockK
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase

    private lateinit var useCase: UpdateBookCoverUseCase

    @MockK
    private lateinit var coverPart: PartData.FileItem

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val oldCoverPath = "covers/old_book.jpg"
    private val existingBookWithCover = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = oldCoverPath,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val existingBookWithoutCover = existingBookWithCover.copy(coverPath = null)
    private val newCoverPath = "covers/new_book.jpg"
    private val updatedBook = existingBookWithCover.copy(coverPath = newCoverPath)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateBookCoverUseCase(bookRepository, coverStorage, getBookByIdUseCase)
    }

    @Test
    fun `when everything is fine, the cover is successfully updated`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverPath) } just Runs
        coEvery { coverStorage.save(coverPart) } returns newCoverPath
        coEvery { bookRepository.updateBookCover(bookId, newCoverPath, language) } returns updatedBook

        val result = useCase.invoke(bookId, coverPart, language)

        assertEquals(updatedBook, result)
        coVerify(exactly = 1) { getBookByIdUseCase.invoke(bookId, language) }
        coVerify(exactly = 1) { coverStorage.delete(oldCoverPath) }
        coVerify(exactly = 1) { coverStorage.save(coverPart) }
        coVerify(exactly = 1) { bookRepository.updateBookCover(bookId, newCoverPath, language) }
    }

    @Test
    fun `when a book is not found, a BookNotFoundException should be thrown`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } throws BookNotFoundException(bookId.toString())

        assertThrows<BookNotFoundException> {
            useCase.invoke(bookId, coverPart, language)
        }

        coVerify(exactly = 0) { coverStorage.delete(any()) }
        coVerify(exactly = 0) { coverStorage.save(any()) }
        coVerify(exactly = 0) { bookRepository.updateBookCover(any(), any(), any()) }
    }

    @Test
    fun `when it is not possible to remove a cover from storage, a StorageException should be thrown`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverPath) } throws StorageException(message = "Error")

        assertThrows<StorageException> {
            useCase.invoke(bookId, coverPart, language)
        }

        coVerify(exactly = 0) { coverStorage.save(any()) }
        coVerify(exactly = 0) { bookRepository.updateBookCover(any(), any(), any()) }
    }

    @Test
    fun `when it is not possible to save a new cover to storage, a StorageException should be thrown`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverPath) } just Runs
        coEvery { coverStorage.save(coverPart) } throws StorageException(message = "Error")

        assertThrows<StorageException> {
            useCase.invoke(bookId, coverPart, language)
        }

        coVerify(exactly = 0) { bookRepository.updateBookCover(any(), any(), any()) }
    }

    @Test
    fun `when repository fails to update book, it should propagate the exception`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns existingBookWithCover
        coEvery { coverStorage.delete(oldCoverPath) } just Runs
        coEvery { coverStorage.save(coverPart) } returns newCoverPath
        coEvery {
            bookRepository.updateBookCover(
                bookId,
                newCoverPath,
                language
            )
        } throws BookNotFoundException(bookId.toString())

        val exception = assertThrows<BookNotFoundException> {
            useCase.invoke(bookId, coverPart, language)
        }

        assertTrue(exception.message!!.contains("$bookId"))
    }

    @Test
    fun `when the book does not have a cover, it is not deleted`() = runTest {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns existingBookWithoutCover
        coEvery { coverStorage.save(coverPart) } returns newCoverPath
        coEvery { bookRepository.updateBookCover(bookId, newCoverPath, language) } returns updatedBook

        val result = useCase.invoke(bookId, coverPart, language)

        assertEquals(updatedBook, result)
        coVerify(exactly = 0) { coverStorage.delete(any()) }
        coVerify(exactly = 1) { coverStorage.save(coverPart) }
        coVerify(exactly = 1) { bookRepository.updateBookCover(bookId, newCoverPath, language) }
    }
}