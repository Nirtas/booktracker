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

package api.routes.genre

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import java.util.*

class GetAllGenresRouteTest : GenresRouteTestBase() {

    private val url = "/api/genres"
    private val userId = UUID.randomUUID()

    @Test
    fun `when genres exist, getAllGenres should return a list of genres and a 200 OK status`() = testApplication {
        val token = generateTestToken(userId)
        val genres = listOf(
            Genre(1, "genre 1"),
            Genre(2, "genre 2"),
            Genre(3, "genre 3")
        )
        val genresDto = GenreMapperImpl().mapGenresToDtos(genres)
        coEvery { getGenresUseCase.invoke(any()) } returns genres

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
            configureRouting()
        }
        val response = client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(genresDto, Json.decodeFromString<List<GenreDto>>(response.bodyAsText()))
    }

    @Test
    fun `when genres not exist, getAllGenres should return an empty list and a 200 OK status`() = testApplication {
        val token = generateTestToken(userId)
        coEvery { getGenresUseCase.invoke(any()) } returns emptyList()

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
            configureRouting()
        }
        val response = client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(emptyList<GenreDto>(), Json.decodeFromString<List<GenreDto>>(response.bodyAsText()))
    }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        val token = generateTestToken(userId)
        coEvery { getGenresUseCase.invoke(any()) } returns emptyList()

        application {
            configureStatusPages()
            configureSerialization()
            configureTestAuthentication()
            configureRouting()
        }
        client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
                append(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
            }
        }

        coVerify(exactly = 1) { getGenresUseCase.invoke("en") }
    }

    @Test
    fun `when getGenresUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            val token = generateTestToken(userId)
            coEvery { getGenresUseCase.invoke(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureTestAuthentication()
                configureRouting()
            }
            val response = client.get(url) {
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
        val response = client.get(url)

        kotlin.test.assertEquals(HttpStatusCode.Unauthorized, response.status)
        kotlin.test.assertEquals(expectedErrorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { getGenresUseCase.invoke(any()) }
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
        val response = client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $invalidToken")
            }
        }

        kotlin.test.assertEquals(HttpStatusCode.Unauthorized, response.status)
        kotlin.test.assertEquals(expectedErrorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { getGenresUseCase.invoke(any()) }
    }
}