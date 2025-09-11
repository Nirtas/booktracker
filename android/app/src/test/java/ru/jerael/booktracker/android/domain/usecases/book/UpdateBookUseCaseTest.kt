package ru.jerael.booktracker.android.domain.usecases.book

import android.net.Uri
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.model.exceptions.AppException
import ru.jerael.booktracker.android.domain.repository.BookRepository
import java.io.File
import java.io.IOException
import java.sql.SQLException

class UpdateBookUseCaseTest {

    @MockK
    private lateinit var repository: BookRepository

    @MockK
    private lateinit var saveCoverFileUseCase: SaveCoverFileUseCase

    @MockK
    private lateinit var errorMapper: ErrorMapper

    private lateinit var useCase: UpdateBookUseCase

    private fun getUpdateParams(): BookUpdateParams = BookUpdateParams(
        id = "book-id",
        title = "Title",
        author = "Author",
        coverUri = null,
        status = BookStatus.READ,
        genreIds = emptyList()
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateBookUseCase(repository, saveCoverFileUseCase, errorMapper)
    }

    @Test
    fun `when cover is present, the book must be successfully updated with the cover`() = runTest {
        val coverFile = mockk<File>()
        val coverUri = mockk<Uri>()
        val bookUpdateParams = getUpdateParams().copy(coverUri = coverUri)
        val payloadSlot = slot<BookUpdatePayload>()
        coEvery { saveCoverFileUseCase.invoke(coverUri) } returns appSuccess(coverFile)
        coEvery { repository.updateBook(capture(payloadSlot)) } returns appSuccess(Unit)

        val result = useCase(bookUpdateParams)

        assertTrue(result.isSuccess)

        val expectedBook = BookUpdatePayload(
            id = bookUpdateParams.id,
            title = bookUpdateParams.title,
            author = bookUpdateParams.author,
            coverFile = coverFile,
            status = bookUpdateParams.status,
            genreIds = bookUpdateParams.genreIds
        )

        assertEquals(expectedBook, payloadSlot.captured)
        coVerify(exactly = 1) { saveCoverFileUseCase.invoke(coverUri) }
        coVerify(exactly = 1) { repository.updateBook(any()) }
    }

    @Test
    fun `when cover is not present, the book must be successfully updated without cover`() =
        runTest {
            val bookUpdateParams = getUpdateParams()
            val payloadSlot = slot<BookUpdatePayload>()
            coEvery { saveCoverFileUseCase.invoke(null) } returns appSuccess(null)
            coEvery { repository.updateBook(capture(payloadSlot)) } returns appSuccess(Unit)

            val result = useCase(bookUpdateParams)

            assertTrue(result.isSuccess)

            val expectedBook = BookUpdatePayload(
                id = bookUpdateParams.id,
                title = bookUpdateParams.title,
                author = bookUpdateParams.author,
                coverFile = null,
                status = bookUpdateParams.status,
                genreIds = bookUpdateParams.genreIds
            )

            assertEquals(expectedBook, payloadSlot.captured)
            coVerify(exactly = 1) { saveCoverFileUseCase.invoke(null) }
            coVerify(exactly = 1) { repository.updateBook(any()) }
        }

    @Test
    fun `when cover saving is failed, saveCoverFileUseCase should return FileStorageError`() =
        runTest {
            val bookUpdateParams = getUpdateParams()
            val storageException = IOException("Error")
            val expectedError = AppError.FileStorageError

            coEvery { saveCoverFileUseCase.invoke(null) } returns Result.failure(storageException)
            coEvery { errorMapper.map(storageException) } returns expectedError

            val result = useCase(bookUpdateParams)

            assertTrue(result.isFailure)
            val throwable = result.exceptionOrNull()
            assertTrue(throwable is AppException)
            val actualError = (throwable as AppException).appError
            assertEquals(expectedError, actualError)
            coVerify(exactly = 0) { repository.updateBook(any()) }
        }

    @Test
    fun `when updateBook is failed, it should return DatabaseError`() = runTest {
        val bookUpdateParams = getUpdateParams()
        val databaseException = SQLException("Error")
        val expectedError = AppError.DatabaseError

        coEvery { saveCoverFileUseCase.invoke(null) } returns appSuccess(null)
        coEvery { repository.updateBook(any()) } throws databaseException
        coEvery { errorMapper.map(databaseException) } returns expectedError

        val result = useCase(bookUpdateParams)

        assertTrue(result.isFailure)
        val throwable = result.exceptionOrNull()
        assertTrue(throwable is AppException)
        val actualError = (throwable as AppException).appError
        assertEquals(expectedError, actualError)
    }
}