package ru.jerael.booktracker.android.domain.usecases.book

import android.net.Uri
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.exceptions.AppException
import ru.jerael.booktracker.android.domain.repository.BookRepository
import java.io.File
import java.sql.SQLException

class AddBookUseCaseTest {

    @MockK
    private lateinit var repository: BookRepository

    @MockK
    private lateinit var saveCoverFileUseCase: SaveCoverFileUseCase

    @MockK
    private lateinit var errorMapper: ErrorMapper

    private lateinit var useCase: AddBookUseCase

    private fun getCreationParams(): BookCreationParams = BookCreationParams(
        title = "Title",
        author = "Author",
        coverUri = null,
        status = BookStatus.READ,
        genreIds = emptyList()
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = AddBookUseCase(repository, saveCoverFileUseCase, errorMapper)
    }

    @Test
    fun `when cover is present, the book must be successfully created with the cover`() = runTest {
        val coverFile = mockk<File>()
        val coverUri = mockk<Uri>()
        val bookCreationParams = getCreationParams().copy(coverUri = coverUri)
        val payloadSlot = slot<BookCreationPayload>()
        val bookId = "book-id"
        coEvery { saveCoverFileUseCase.invoke(coverUri) } returns appSuccess(coverFile)
        coEvery { repository.addBook(capture(payloadSlot)) } returns appSuccess(bookId)

        val result = useCase(bookCreationParams)

        assertTrue(result.isSuccess)
        assertEquals(bookId, result.getOrNull())
        assertEquals(coverFile, payloadSlot.captured.coverFile)
        coVerify(exactly = 1) { saveCoverFileUseCase.invoke(coverUri) }
        coVerify(exactly = 1) { repository.addBook(any()) }
    }

    @Test
    fun `when cover is not present, the book must be successfully created without cover`() =
        runTest {
            val bookCreationParams = getCreationParams()
            val payloadSlot = slot<BookCreationPayload>()
            val bookId = "book-id"
            coEvery { saveCoverFileUseCase.invoke(null) } returns appSuccess(null)
            coEvery { repository.addBook(capture(payloadSlot)) } returns appSuccess(bookId)

            val result = useCase(bookCreationParams)

            assertTrue(result.isSuccess)
            assertEquals(bookId, result.getOrNull())
            assertNull(payloadSlot.captured.coverFile)
            coVerify(exactly = 1) { saveCoverFileUseCase.invoke(null) }
            coVerify(exactly = 1) { repository.addBook(any()) }
        }

    @Test
    fun `when cover saving is failed, saveCoverFileUseCase should return FileStorageError`() =
        runTest {
            val bookCreationParams = getCreationParams()
            val storageException = IOException("Error")
            val expectedError = AppError.FileStorageError

            coEvery { saveCoverFileUseCase.invoke(null) } returns Result.failure(storageException)
            coEvery { errorMapper.map(storageException) } returns expectedError

            val result = useCase(bookCreationParams)

            assertTrue(result.isFailure)
            val throwable = result.exceptionOrNull()
            assertTrue(throwable is AppException)
            val actualError = (throwable as AppException).appError
            assertEquals(expectedError, actualError)
            coVerify(exactly = 0) { repository.addBook(any()) }
        }

    @Test
    fun `when addBook is failed, it should return DatabaseError`() = runTest {
        val bookCreationParams = getCreationParams()
        val databaseException = SQLException("Error")
        val expectedError = AppError.DatabaseError

        coEvery { saveCoverFileUseCase.invoke(null) } returns appSuccess(null)
        coEvery { repository.addBook(any()) } throws databaseException
        coEvery { errorMapper.map(databaseException) } returns expectedError

        val result = useCase(bookCreationParams)

        assertTrue(result.isFailure)
        val throwable = result.exceptionOrNull()
        assertTrue(throwable is AppException)
        val actualError = (throwable as AppException).appError
        assertEquals(expectedError, actualError)
    }
}