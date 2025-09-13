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

package ru.jerael.booktracker.android.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.android.data.remote.HttpRoute
import ru.jerael.booktracker.android.data.remote.dto.ErrorDto
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto

class GenreApiServiceImplTest {

    private val firstGenre = GenreDto(id = 1, name = "gaming")
    private val secondGenre = GenreDto(id = 2, name = "adventure")
    private val thirdGenre = GenreDto(id = 3, name = "science fiction")

    private fun createApiService(mockEngine: MockEngine): GenreApiService {
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
        return GenreApiServiceImpl(httpClient)
    }

    private fun MockRequestHandleScope.respondWithError(): HttpResponseData {
        val errorJson = Json.encodeToString(ErrorDto("ERROR_CODE", "Error"))
        return respond(
            content = ByteReadChannel(errorJson.toByteArray()),
            status = HttpStatusCode.BadRequest,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    @Test
    fun `when server responds 200 OK, getGenres should return list of genres`() = runTest {
        val genreDtos = listOf(firstGenre, secondGenre, thirdGenre)
        val expectedJsonResponse = Json.encodeToString(genreDtos)

        val mockEngine = MockEngine { request ->
            assertEquals(HttpRoute.GENRES, request.url.encodedPath)
            assertEquals(HttpMethod.Get, request.method)

            respond(
                content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val api = createApiService(mockEngine)

        val genres = api.getGenres()

        assertEquals(genreDtos.size, genres.size)
        assertEquals(genreDtos, genres)
    }

    @Test
    fun `when server responds with an error, getGenres should throw a ClientRequestException`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.GENRES, request.url.encodedPath)
                assertEquals(HttpMethod.Get, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.getGenres()
            }
        }
}