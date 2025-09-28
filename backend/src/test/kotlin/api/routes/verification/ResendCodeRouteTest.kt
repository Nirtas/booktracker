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
import io.mockk.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.verification.VerificationResendCodeDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import kotlin.test.assertEquals

class ResendCodeRouteTest : VerificationsRouteTestBase() {

    private val url = "/api/verifications/resend"
    private val json = Json.encodeToString(VerificationResendCodeDto("test@example.com"))
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid, resendCode should return a 200 OK status`() =
        testApplication {
            coEvery { resendVerificationCodeUseCase.invoke(any()) } just Runs

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
            verify(exactly = 1) { verificationValidator.validateResending(any()) }
            coVerify(exactly = 1) { resendVerificationCodeUseCase.invoke(any()) }
        }

    @Test
    fun `when validateVerification is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            every { verificationValidator.validateResending(any()) } throws Exception("Error")

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
            coVerify(exactly = 0) { resendVerificationCodeUseCase.invoke(any()) }
        }

    @Test
    fun `when resendVerificationCodeUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { resendVerificationCodeUseCase.invoke(any()) } throws Exception("Error")

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