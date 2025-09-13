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