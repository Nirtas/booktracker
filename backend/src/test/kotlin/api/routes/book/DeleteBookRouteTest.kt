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

package api.routes.book

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import java.util.*

class DeleteBookRouteTest : BooksRouteTestBase() {

    private val userId = UUID.randomUUID()
    private val bookId = UUID.randomUUID()
    private val url = "/api/books/$bookId"

    @Test
    fun `when a book successfully deleted, deleteBook should return a 204 No Content status`() = testApplication {
        val token = generateTestToken(userId)
        coEvery { deleteBookUseCase.invoke(userId, bookId) } just Runs

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
        }

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `when deleteBookUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            val token = generateTestToken(userId)
            coEvery { deleteBookUseCase.invoke(any(), any()) } throws Exception("Error")

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
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            val errorDto = ErrorDto(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred. Please try again later."
            )
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
        coVerify(exactly = 0) { deleteBookUseCase.invoke(any(), any()) }
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
        coVerify(exactly = 0) { deleteBookUseCase.invoke(any(), any()) }
    }
}