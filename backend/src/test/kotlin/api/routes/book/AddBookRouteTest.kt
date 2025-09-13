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

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.parsing.ParsedBookCreationRequest
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class AddBookRouteTest : BooksRouteTestBase() {

    private val bookCreationDto = BookCreationDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )
    private val createdBook = Book(
        id = UUID.randomUUID(),
        title = bookCreationDto.title,
        author = bookCreationDto.author,
        coverPath = null,
        status = BookStatus.fromString(bookCreationDto.status)!!,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val createdBookDto = BookMapperImpl(imageBaseUrl, GenreMapperImpl()).mapBookToDto(createdBook)
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )
    private val url = "/api/books"

    @Test
    fun `when request is valid with cover image, addBook should return the created book and a 201 Created status`() =
        testApplication {
            val coverBytesSlot = slot<ByteArray?>()
            coEvery { addBookUseCase.invoke(any(), captureNullable(coverBytesSlot), any(), any()) } returns createdBook

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url) {
                val multipartBody = MultiPartFormDataContent(
                    parts = formData {
                        append(
                            "book",
                            Json.encodeToString(bookCreationDto),
                            Headers.build {
                                append(HttpHeaders.ContentType, "application/json")
                            }
                        )
                        append(
                            "cover",
                            "image content".toByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"cover.jpg\"")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.Created, response.status)
            assertEquals(createdBookDto, Json.decodeFromString<BookDto>(response.bodyAsText()))
            assertNotNull(coverBytesSlot.captured)
        }

    @Test
    fun `when request is valid without cover image, addBook should return the created book and a 201 Created status`() =
        testApplication {
            val parsedBookCreationRequest = ParsedBookCreationRequest(
                bookCreationDto = bookCreationDto,
                coverBytes = null,
                coverFileName = null
            )
            coEvery { multipartParser.parseBookCreation(any()) } returns parsedBookCreationRequest
            val coverBytesSlot = slot<ByteArray?>()
            coEvery { addBookUseCase.invoke(any(), captureNullable(coverBytesSlot), any(), any()) } returns createdBook

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url) {
                val multipartBody = MultiPartFormDataContent(
                    parts = formData {
                        append(
                            "book",
                            Json.encodeToString(bookCreationDto),
                            Headers.build {
                                append(HttpHeaders.ContentType, "application/json")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.Created, response.status)
            assertEquals(createdBookDto, Json.decodeFromString<BookDto>(response.bodyAsText()))
            assertNull(coverBytesSlot.captured)
        }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        coEvery { addBookUseCase.invoke(any(), any(), any(), any()) } returns createdBook

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        client.post(url) {
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
        }

        coVerify(exactly = 1) { addBookUseCase.invoke(any(), any(), any(), "en") }
    }

    @Test
    fun `when multipart parsing is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { multipartParser.parseBookCreation(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
            verify(exactly = 0) { bookValidator.validateCreation(any()) }
            coVerify(exactly = 0) { addBookUseCase.invoke(any(), any(), any(), any()) }
        }

    @Test
    fun `when validateCreation is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            every { bookValidator.validateCreation(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
            coVerify(exactly = 0) { addBookUseCase.invoke(any(), any(), any(), any()) }
        }

    @Test
    fun `when addBookUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { addBookUseCase.invoke(any(), any(), any(), any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }
}