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
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import java.time.Instant
import java.util.*

class GetAllBooksRouteTest : BooksRouteTestBase() {

    private val url = "/api/books"

    @Test
    fun `when books exist, getAllBooks should return a list of books and a 200 OK status`() = testApplication {
        val books = listOf(
            Book(
                id = UUID.randomUUID(),
                title = "Title",
                author = "Author",
                coverPath = null,
                status = BookStatus.READ,
                createdAt = Instant.now(),
                genres = emptyList()
            ),
            Book(
                id = UUID.randomUUID(),
                title = "Title 2",
                author = "Author 2",
                coverPath = null,
                status = BookStatus.READ,
                createdAt = Instant.now(),
                genres = listOf(
                    Genre(1, "genre 1"),
                    Genre(2, "genre 2"),
                    Genre(3, "genre 3")
                )
            )
        )
        val booksDto = BookMapperImpl(imageBaseUrl, GenreMapperImpl()).mapBooksToDtos(books)
        coEvery { getBooksUseCase.invoke(any()) } returns books

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.get(url)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(booksDto, Json.decodeFromString<List<BookDto>>(response.bodyAsText()))
    }

    @Test
    fun `when books not exist, getAllBooks should return an empty list and a 200 OK status`() = testApplication {
        coEvery { getBooksUseCase.invoke(any()) } returns emptyList()

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.get(url)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(emptyList<BookDto>(), Json.decodeFromString<List<BookDto>>(response.bodyAsText()))
    }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        coEvery { getBooksUseCase.invoke(any()) } returns emptyList()

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        client.get(url) {
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
        }

        coVerify(exactly = 1) { getBooksUseCase.invoke("en") }
    }

    @Test
    fun `when getBooksUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { getBooksUseCase.invoke(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.get(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            val errorDto = ErrorDto(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred. Please try again later."
            )
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }
}