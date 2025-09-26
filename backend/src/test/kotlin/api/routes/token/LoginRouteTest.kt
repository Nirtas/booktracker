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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.login.LoginRequestDto
import ru.jerael.booktracker.backend.api.dto.login.LoginResponseDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import kotlin.test.assertEquals

class LoginRouteTest : TokensRouteTestBase() {

    private val url = "/api/tokens"
    private val json = Json.encodeToString(LoginRequestDto("test@example.com", "Passw0rd!"))
    private val token = TokenPair(accessToken = "access", refreshToken = "refresh")
    private val loginResponseDto = LoginResponseDto(token.accessToken, token.refreshToken)
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid and credentials are valid for a verified user, login should return a token and a 200 OK status`() =
        testApplication {
            coEvery { loginUseCase.invoke(any()) } returns token
            every { tokenMapper.mapTokenToResponseDto(token) } returns loginResponseDto

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
            verify(exactly = 1) { loginValidator.validateLogin(any()) }
            verify(exactly = 1) { loginMapper.mapDtoToPayload(any()) }
            coVerify(exactly = 1) { loginUseCase.invoke(any()) }
            verify(exactly = 1) { tokenMapper.mapTokenToResponseDto(any()) }
        }

    @Test
    fun `when validateLogin is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        every { loginValidator.validateLogin(any()) } throws Exception("Error")

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
        coVerify(exactly = 0) { loginUseCase.invoke(any()) }
    }

    @Test
    fun `when loginUseCase is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        coEvery { loginUseCase.invoke(any()) } throws Exception("Error")

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
}