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