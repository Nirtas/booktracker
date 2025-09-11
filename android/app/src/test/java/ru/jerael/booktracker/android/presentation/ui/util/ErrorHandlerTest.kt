package ru.jerael.booktracker.android.presentation.ui.util

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import okio.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError

class ErrorHandlerTest {

    @MockK
    private lateinit var provider: StringResourceProvider

    @MockK
    private lateinit var errorMapper: ErrorMapper

    private lateinit var handler: ErrorHandler

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        handler = ErrorHandler(provider, errorMapper)
    }

    @Test
    fun `when handleError is called with a network exception, it should return the network error string`() =
        runTest {
            val errorString = "Error"
            val exception = IOException(errorString)
            val mappedError = AppError.NetworkError
            coEvery { errorMapper.map(exception) } returns mappedError
            every { provider.getString(mappedError) } returns errorString

            val result = handler.handleError(exception)

            assertEquals(errorString, result)
        }

    @Test
    fun `when handleError is called with an unknown exception, it should return the unknown error string`() =
        runTest {
            val errorString = "Error"
            val exception = RuntimeException(errorString)
            val mappedError = AppError.UnknownError
            coEvery { errorMapper.map(exception) } returns mappedError
            every { provider.getString(mappedError) } returns errorString

            val result = handler.handleError(exception)

            assertEquals(errorString, result)
        }
}