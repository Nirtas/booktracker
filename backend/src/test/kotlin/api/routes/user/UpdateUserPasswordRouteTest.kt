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

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdatePasswordDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import kotlin.test.assertEquals

class UpdateUserPasswordRouteTest : UsersRouteTestBase() {

    private val userId = "661d6c9d-c4e9-4921-93c5-8b3dd4e57bf3"
    private val url = "/api/users/$userId/password"
    private val json = Json.encodeToString(
        UserUpdatePasswordDto("test@example.com", "Passw0rd!", "Passw0rd@")
    )
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid, updateUserPassword should return a 200 OK status`() = testApplication {
        coEvery { updateUserPasswordUseCase.invoke(any()) } just Runs

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.put(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        verify(exactly = 1) { userValidator.validateUpdatePassword(any()) }
        verify(exactly = 1) { userMapper.mapUpdatePasswordDtoToUpdatePasswordPayload(any(), any()) }
        coVerify(exactly = 1) { updateUserPasswordUseCase.invoke(any()) }
    }

    @Test
    fun `when validateUpdatePassword is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            every { userValidator.validateUpdatePassword(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
            coVerify(exactly = 0) { updateUserPasswordUseCase.invoke(any()) }
        }

    @Test
    fun `when updateUserPasswordUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { updateUserPasswordUseCase.invoke(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }
}