package ru.jerael.booktracker.android.domain.util

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.exceptions.AppException

class ResultExtensionsTest {

    private val errorMapper: ErrorMapper = mockk()

    @Test
    fun `when throwable is an AppException, then the original AppError must be returned without mapping`() =
        runTest {
            val expectedError = AppError.UnknownError
            val expectedException = AppException(expectedError)

            val actualError = expectedException.toAppError(errorMapper)

            assertEquals(expectedError, actualError)
            coVerify(exactly = 0) { errorMapper.map(any()) }
        }

    @Test
    fun `when throwable is a generic exception, then it must be mapped to an AppError using the mapper`() =
        runTest {
            val exception = IOException("Error")
            val expectedError = AppError.NetworkError
            coEvery { errorMapper.map(exception) } returns expectedError

            val actualError = exception.toAppError(errorMapper)

            assertEquals(expectedError, actualError)
            coVerify(exactly = 1) { errorMapper.map(exception) }
        }
}