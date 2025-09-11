package ru.jerael.booktracker.android.presentation.ui.util

import android.content.Context
import androidx.annotation.StringRes
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.AppError
import java.util.stream.Stream

class StringResourceProviderTest {

    private lateinit var context: Context
    private lateinit var provider: StringResourceProvider

    @BeforeEach
    fun setUp() {
        context = mockk()
        provider = StringResourceProvider(context)
    }

    companion object {

        @JvmStatic
        private fun provideErrorsAndStringResIds(): Stream<Arguments> = Stream.of(
            Arguments.of(AppError.NetworkError, "NetworkError", R.string.error_network),
            Arguments.of(AppError.UnknownError, "UnknownError", R.string.error_unknown),
            Arguments.of(AppError.DatabaseError, "DatabaseError", R.string.error_database),
            Arguments.of(
                AppError.FileStorageError,
                "FileStorageError",
                R.string.error_file_storage
            ),
            Arguments.of(AppError.NotFoundError, "NotFoundError", R.string.error_not_found)
        )
    }

    @ParameterizedTest(name = "[{index}] Error: {1} => StringResId: {2}")
    @MethodSource("provideErrorsAndStringResIds")
    fun `when getString is called with simple AppError, it should return the correct resId`(
        error: AppError,
        errorName: String,
        @StringRes resId: Int
    ) =
        runTest {
            every { context.getString(resId) } returns errorName

            val result = provider.getString(error)

            assertEquals(errorName, result)
        }

    @Test
    fun `when appError is ServerError with a known code, it should return the server error string`() =
        runTest {
            val errorCode = "ERROR"
            val errorString = "Error message"
            val resId = 123
            val packageName = "name.package"
            every { context.packageName } returns packageName
            every {
                context.resources.getIdentifier(
                    "error_code_$errorCode",
                    "string",
                    packageName
                )
            } returns resId
            every { context.getString(resId) } returns errorString

            val result = provider.getString(AppError.ServerError(errorCode))

            assertEquals(errorString, result)
        }

    @Test
    fun `when appError is ServerError with an unknown code, it should return the unknown error string`() =
        runTest {
            val errorCode = "ERROR"
            val errorString = "Error message"
            val packageName = "name.package"
            every { context.packageName } returns packageName
            every {
                context.resources.getIdentifier(
                    "error_code_$errorCode",
                    "string",
                    packageName
                )
            } returns 0
            every { context.getString(R.string.error_unknown) } returns errorString

            val result = provider.getString(AppError.ServerError(errorCode))

            assertEquals(errorString, result)
        }
}