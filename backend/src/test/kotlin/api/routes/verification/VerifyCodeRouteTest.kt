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

package api.routes.verification

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.login.LoginResponseDto
import ru.jerael.booktracker.backend.api.dto.verification.VerificationDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import kotlin.test.assertEquals

class VerifyCodeRouteTest : VerificationsRouteTestBase() {

    private val url = "/api/verifications"
    private val json = Json.encodeToString(VerificationDto("test@example.com", "123456"))
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid, verify should return new token pair and a 200 OK status`() =
        testApplication {
            val tokenPair = TokenPair("access", "refresh")
            val responseDto = LoginResponseDto(tokenPair.accessToken, tokenPair.refreshToken)
            coEvery { verifyCodeUseCase.invoke(any()) } returns tokenPair
            every { tokenMapper.mapTokenToResponseDto(tokenPair) } returns responseDto

            application {
                configureStatusPages()
                configureSerialization()
                configureTestAuthentication()
                configureRouting()
            }
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(responseDto, Json.decodeFromString<LoginResponseDto>(response.bodyAsText()))
            coVerify(exactly = 1) { verifyCodeUseCase.invoke(any()) }
        }

    @Test
    fun `when loginUseCase is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        coEvery { verifyCodeUseCase.invoke(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
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