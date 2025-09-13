/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.android.data.mappers

import android.database.sqlite.SQLiteException
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.Source
import okio.IOException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import java.util.stream.Stream

class ErrorMapperImplTest {

    private lateinit var errorMapper: ErrorMapper

    private fun <T : ResponseException> setupMockedException(
        exception: T,
        errorJson: String
    ) {
        val mockkResponse = mockk<HttpResponse>()
        val mockkCall = mockk<HttpClientCall>()
        val mockkHeaders = mockk<Headers>()
        val source: Source = Buffer().apply { write(errorJson.encodeToByteArray()) }

        every { mockkResponse.call } returns mockkCall
        every { exception.response } returns mockkResponse
        every { mockkResponse.headers } returns mockkHeaders
        every { mockkHeaders[HttpHeaders.ContentType] } returns "application/json"
        coEvery { mockkCall.bodyNullable(any()) } returns source
    }

    companion object {

        @JvmStatic
        private fun provideExceptionsAndExpectedErrors(): Stream<Arguments> = Stream.of(
            Arguments.of(IOException("Error"), AppError.NetworkError),
            Arguments.of(SQLiteException("Error"), AppError.DatabaseError),
            Arguments.of(RuntimeException("Error"), AppError.UnknownError)
        )
    }

    @BeforeEach
    fun setUp() {
        errorMapper = ErrorMapperImpl()
    }

    @ParameterizedTest(name = "[{index}] Exception: \"{0}\" => Error: {1}")
    @MethodSource("provideExceptionsAndExpectedErrors")
    fun `when throwable is given, then it should be mapped to correct AppError`(
        exception: Throwable,
        expectedError: AppError
    ) = runTest {
        val actualError = errorMapper.map(exception)

        assertEquals(expectedError, actualError)
    }

    @Test
    fun `when throwable is Ktor exception with blank response body, then it should be mapped to UnknownError`() =
        runTest {
            val mockkException = mockk<ClientRequestException>()
            val errorJson = ""
            setupMockedException(mockkException, errorJson)

            val appError = errorMapper.map(mockkException)

            assertEquals(AppError.UnknownError, appError)
        }

    @Test
    fun `when throwable is Ktor exception with valid error json, then it should be mapped to ServerError`() =
        runTest {
            val mockkException = mockk<ClientRequestException>()
            val errorJson = """{"code": "ERROR_CODE", "message": "Error"}"""
            setupMockedException(mockkException, errorJson)

            val appError = errorMapper.map(mockkException)

            assertTrue(appError is AppError.ServerError)
            assertEquals("ERROR_CODE", (appError as AppError.ServerError).code)
        }

    @Test
    fun `when throwable is Ktor exception with malformed json, then it should be mapped to UnknownError`() =
        runTest {
            val mockkException = mockk<ClientRequestException>()
            val errorJson = """{"code": "ERROR_CODE", "message": Missing quote}"""
            setupMockedException(mockkException, errorJson)

            val appError = errorMapper.map(mockkException)

            assertEquals(AppError.UnknownError, appError)
        }
}