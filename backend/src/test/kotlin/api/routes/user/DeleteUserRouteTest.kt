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

package api.routes.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.user.UserDeletionDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import java.util.*
import kotlin.test.assertEquals

class DeleteUserRouteTest : UsersRouteTestBase() {

    private val userId = UUID.randomUUID()
    private val url = "/api/users/me"
    private val json = Json.encodeToString(
        UserDeletionDto("Passw0rd!")
    )
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid, deleteUser should return a 200 OK status`() = testApplication {
        val token = generateTestToken(userId)
        coEvery { deleteUserUseCase.invoke(any()) } just Runs

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
            configureRouting()
        }
        val response = client.delete(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.NoContent, response.status)
        verify(exactly = 1) { userValidator.validateDeletion(any()) }
        coVerify(exactly = 1) { deleteUserUseCase.invoke(any()) }
    }

    @Test
    fun `when validateDeletion is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            val token = generateTestToken(userId)
            every { userValidator.validateDeletion(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureTestAuthentication()
                configureRouting()
            }
            val response = client.delete(url) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
            coVerify(exactly = 0) { deleteUserUseCase.invoke(any()) }
        }

    @Test
    fun `when deleteUserUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            val token = generateTestToken(userId)
            coEvery { deleteUserUseCase.invoke(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureTestAuthentication()
                configureRouting()
            }
            val response = client.delete(url) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }

    @Test
    fun `when Authorization header is missing, it should return 401 Unauthorized`() = testApplication {
        val expectedErrorDto = ErrorDto(
            code = "INVALID_TOKEN",
            message = "Token is not valid or has expired."
        )

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
            configureRouting()
        }
        val response = client.delete(url)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedErrorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { deleteUserUseCase.invoke(any()) }
    }

    @Test
    fun `when token is missing userId claim, it should return 401 Unauthorized`() = testApplication {
        val expectedErrorDto = ErrorDto(
            code = "INVALID_TOKEN",
            message = "Token is not valid or has expired."
        )
        val invalidToken = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(Date(System.currentTimeMillis() + 15L * 60 * 1000))
            .sign(Algorithm.HMAC256(secret))

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
            configureRouting()
        }
        val response = client.delete(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $invalidToken")
            }
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(expectedErrorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { deleteUserUseCase.invoke(any()) }
    }
}