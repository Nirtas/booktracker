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

package api.routes.token

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.login.LoginResponseDto
import ru.jerael.booktracker.backend.api.dto.token.RefreshTokenDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import kotlin.test.assertEquals

class RefreshTokenRouteTest : TokensRouteTestBase() {

    private val url = "/api/tokens/refresh"
    private val refreshTokenDto = RefreshTokenDto("token")
    private val json = Json.encodeToString(refreshTokenDto)
    private val token = TokenPair(accessToken = "access", refreshToken = "refresh")
    private val loginResponseDto = LoginResponseDto(token.accessToken, token.refreshToken)
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid, refreshToken should return a new token pair and a 200 OK status`() = testApplication {
        coEvery { refreshTokenUseCase.invoke(any()) } returns token
        every { tokenMapper.mapTokenToResponseDto(token) } returns loginResponseDto
        every { tokenValidator.validateRefresh(any()) } just Runs

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(loginResponseDto, Json.decodeFromString<LoginResponseDto>(response.bodyAsText()))
        verify(exactly = 1) { tokenValidator.validateRefresh(refreshTokenDto) }
        coVerify(exactly = 1) { refreshTokenUseCase.invoke(refreshTokenDto.refreshToken) }
        verify(exactly = 1) { tokenMapper.mapTokenToResponseDto(token) }
    }

    @Test
    fun `when refresh token is invalid, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { refreshTokenUseCase.invoke(any()) } throws Exception("Error")
            every { tokenValidator.validateRefresh(any()) } just Runs

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }

    @Test
    fun `when validation fails, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        every { tokenValidator.validateRefresh(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        coVerify(exactly = 0) { refreshTokenUseCase.invoke(any()) }
    }
}