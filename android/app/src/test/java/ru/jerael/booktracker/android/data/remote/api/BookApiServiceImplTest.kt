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
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.android.data.remote.HttpRoute
import ru.jerael.booktracker.android.data.remote.dto.ErrorDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsCreationDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import java.io.File
import java.time.Instant

class BookApiServiceImplTest {

    private val bookId = "e607ac1d-309f-4301-b71f-52b59e7cb4db"
    private val bookDto = BookDto(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = null,
        status = BookStatus.WANT_TO_READ.value,
        createdAt = Instant.now().toEpochMilli(),
        genres = listOf(
            GenreDto(id = 1, name = "gaming"),
            GenreDto(id = 2, name = "adventure"),
            GenreDto(id = 3, name = "science fiction")
        )
    )

    private fun createApiService(mockEngine: MockEngine): BookApiService {
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
        return BookApiServiceImpl(httpClient)
    }

    private fun createTestFile(content: String): File {
        return File.createTempFile("cover", ".jpg").apply {
            writeText(content)
            deleteOnExit()
        }
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
    fun `when server responds 200 OK, getBooks should return list of books`() = runTest {
        val bookDtos = listOf(bookDto)
        val expectedJsonResponse = Json.encodeToString(bookDtos)

        val mockEngine = MockEngine { request ->
            assertEquals(HttpRoute.BOOKS, request.url.encodedPath)
            assertEquals(HttpMethod.Get, request.method)

            respond(
                content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val api = createApiService(mockEngine)

        val books = api.getBooks()

        assertEquals(bookDtos.size, books.size)
        assertEquals(bookDto, books.find { it.id == bookId })
    }

    @Test
    fun `when server responds 200 OK with empty list, getBooks should return empty list`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.BOOKS, request.url.encodedPath)
                assertEquals(HttpMethod.Get, request.method)

                respond(
                    content = ByteReadChannel("[]".toByteArray()),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = createApiService(mockEngine)

            val books = api.getBooks()

            assertTrue(books.isEmpty())
        }

    @Test
    fun `when server responds with an error, getBooks should throw a ClientRequestException`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.BOOKS, request.url.encodedPath)
                assertEquals(HttpMethod.Get, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.getBooks()
            }
        }

    @Test
    fun `when adding book with details and cover file, addBook should send correct multipart request and return created book`() =
        runTest {
            val bookDetailsCreationDto = BookDetailsCreationDto(
                title = bookDto.title,
                author = bookDto.author,
                status = bookDto.status,
                genreIds = bookDto.genres.map { it.id }
            )
            val coverFileContent = "file content"
            val coverFile = createTestFile(coverFileContent)
            val expectedBookDto = bookDto.copy(coverUrl = coverFile.absolutePath)
            val expectedJsonResponse = Json.encodeToString(expectedBookDto)

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.BOOKS, request.url.encodedPath)
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.body is MultiPartFormDataContent)

                val body = request.body.toByteArray().decodeToString()

                assertTrue(body.contains("Content-Type: application/json"))
                assertTrue(body.contains("Content-Disposition: form-data; name=book"))
                assertTrue(body.contains(Json.encodeToString(bookDetailsCreationDto)))

                assertTrue(body.contains("Content-Type: image/jpeg"))
                assertTrue(body.contains("Content-Disposition: form-data; name=cover; filename=\"${coverFile.name}\""))
                assertTrue(body.contains(coverFileContent))

                respond(
                    content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = createApiService(mockEngine)

            val createdBook = api.addBook(bookDetailsCreationDto, coverFile)

            assertEquals(expectedBookDto.id, createdBook.id)
        }

    @Test
    fun `when adding book with details only, addBook should send multipart request with only book part and return created book`() =
        runTest {
            val bookDetailsCreationDto = BookDetailsCreationDto(
                title = bookDto.title,
                author = bookDto.author,
                status = bookDto.status,
                genreIds = bookDto.genres.map { it.id }
            )
            val expectedJsonResponse = Json.encodeToString(bookDto)

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.BOOKS, request.url.encodedPath)
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.body is MultiPartFormDataContent)

                val body = request.body.toByteArray().decodeToString()

                assertTrue(body.contains("Content-Type: application/json"))
                assertTrue(body.contains("Content-Disposition: form-data; name=book"))
                assertTrue(body.contains(Json.encodeToString(bookDetailsCreationDto)))

                respond(
                    content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                    status = HttpStatusCode.Created,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = createApiService(mockEngine)

            val createdBook = api.addBook(bookDetailsCreationDto, null)

            assertEquals(bookId, createdBook.id)
        }

    @Test
    fun `when server responds with an error on book creation, addBook should throw a ClientRequestException`() =
        runTest {
            val bookDetailsCreationDto = BookDetailsCreationDto(
                title = bookDto.title,
                author = bookDto.author,
                status = bookDto.status,
                genreIds = bookDto.genres.map { it.id }
            )

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.BOOKS, request.url.encodedPath)
                assertEquals(HttpMethod.Post, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.addBook(bookDetailsCreationDto, null)
            }
        }

    @Test
    fun `when server responds 200 OK, getBookById should return a book dto`() = runTest {
        val expectedJsonResponse = Json.encodeToString(bookDto)

        val mockEngine = MockEngine { request ->
            assertEquals(HttpRoute.bookById(bookId), request.url.encodedPath)
            assertEquals(HttpMethod.Get, request.method)

            respond(
                content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val api = createApiService(mockEngine)

        val book = api.getBookById(bookId)

        assertEquals(bookDto, book)
    }

    @Test
    fun `when server responds with an error, getBookById should throw a ClientRequestException`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookById(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Get, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.getBookById(bookId)
            }
        }

    @Test
    fun `when updating details, updateBookDetails should send PUT request with correct json body`() =
        runTest {
            val bookDetailsUpdateDto = BookDetailsUpdateDto(
                title = "Title new",
                author = "Author new",
                status = BookStatus.READING.value,
                genreIds = emptyList()
            )
            val expectedBookDto = bookDto.copy(
                title = bookDetailsUpdateDto.title,
                author = bookDetailsUpdateDto.author,
                status = bookDetailsUpdateDto.status,
                genres = emptyList()
            )
            val expectedJsonResponse = Json.encodeToString(expectedBookDto)

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookById(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Put, request.method)

                respond(
                    content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = createApiService(mockEngine)

            val updatedBook = api.updateBookDetails(expectedBookDto.id, bookDetailsUpdateDto)

            assertEquals(expectedBookDto, updatedBook)
        }

    @Test
    fun `when server responds with an error on book details update, updateBookDetails should throw a ClientRequestException`() =
        runTest {
            val bookDetailsUpdateDto = BookDetailsUpdateDto(
                title = "Title new",
                author = "Author new",
                status = BookStatus.READING.value,
                genreIds = emptyList()
            )

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookById(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Put, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.updateBookDetails(bookId, bookDetailsUpdateDto)
            }
        }

    @Test
    fun `when updating cover, updateBookCover should send POST request with correct multipart form data`() =
        runTest {
            val coverFileContent = "file content"
            val coverFile = createTestFile(coverFileContent)
            val expectedBookDto = bookDto.copy(coverUrl = coverFile.absolutePath)
            val expectedJsonResponse = Json.encodeToString(expectedBookDto)

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookCover(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Post, request.method)
                assertTrue(request.body is MultiPartFormDataContent)

                val body = request.body.toByteArray().decodeToString()

                assertTrue(body.contains("Content-Type: image/jpeg"))
                assertTrue(body.contains("Content-Disposition: form-data; name=cover; filename=\"${coverFile.name}\""))
                assertTrue(body.contains(coverFileContent))

                respond(
                    content = ByteReadChannel(expectedJsonResponse.toByteArray()),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = createApiService(mockEngine)

            val updatedBook = api.updateBookCover(bookId, coverFile)

            assertEquals(expectedBookDto, updatedBook)
        }

    @Test
    fun `when server responds with an error on book cover update, updateBookCover should throw a ClientRequestException`() =
        runTest {
            val coverFileContent = "file content"
            val coverFile = createTestFile(coverFileContent)

            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookCover(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Post, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.updateBookCover(bookId, coverFile)
            }
        }

    @Test
    fun `when server responds 204 No Content, deleteBook should complete successfully without returning value`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookById(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Delete, request.method)

                respond(
                    content = ByteReadChannel(""),
                    status = HttpStatusCode.NoContent,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val api = createApiService(mockEngine)

            assertDoesNotThrow {
                api.deleteBook(bookId)
            }
        }

    @Test
    fun `when server responds with an error on book deletion, deleteBook should throw a ClientRequestException`() =
        runTest {
            val mockEngine = MockEngine { request ->
                assertEquals(HttpRoute.bookById(bookId), request.url.encodedPath)
                assertEquals(HttpMethod.Delete, request.method)

                respondWithError()
            }
            val api = createApiService(mockEngine)

            assertThrows<ClientRequestException> {
                api.deleteBook(bookId)
            }
        }
}