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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.user.UserCreationDto
import ru.jerael.booktracker.backend.api.dto.user.UserDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.user.User
import java.util.*
import kotlin.test.assertEquals

class RegisterRouteTest : UsersRouteTestBase() {

    private val url = "/api/users"
    private val json = Json.encodeToString(UserCreationDto("test@example.com", "Passw0rd!"))
    private val newUser = User(
        id = UUID.fromString("661d6c9d-c4e9-4921-93c5-8b3dd4e57bf3"),
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = false
    )
    private val userDto = UserDto(
        id = newUser.id.toString(),
        email = newUser.email,
        isVerified = newUser.isVerified
    )
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @Test
    fun `when request is valid, register should return the created user and a 201 Created status`() = testApplication {
        coEvery { registerUserUseCase.invoke(any()) } returns newUser
        every { userMapper.mapUserToDto(newUser) } returns userDto

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertEquals(userDto, Json.decodeFromString<UserDto>(response.bodyAsText()))
        verify(exactly = 1) { userValidator.validateCreation(any()) }
        verify(exactly = 1) { userMapper.mapCreationDtoToCreationPayload(any()) }
        coVerify(exactly = 1) { registerUserUseCase.invoke(any()) }
        verify(exactly = 1) { userMapper.mapUserToDto(any()) }
    }

    @Test
    fun `when validateCreation is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            every { userValidator.validateCreation(any()) } throws Exception("Error")

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
            coVerify(exactly = 0) { registerUserUseCase.invoke(any()) }
        }

    @Test
    fun `when registerUserUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { registerUserUseCase.invoke(any()) } throws Exception("Error")

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