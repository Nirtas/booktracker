package api.routes.book

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.verify
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.time.Instant
import java.util.*
import kotlin.test.assertEquals

class UpdateBookDetailsRouteTest : BooksRouteTestBase() {

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val bookUpdateDto = BookUpdateDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )
    private val updatedBook = Book(
        id = bookId,
        title = bookUpdateDto.title,
        author = bookUpdateDto.author,
        coverPath = null,
        status = BookStatus.fromString(bookUpdateDto.status)!!,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )
    private val url = "/api/books/$bookId"

    @Test
    fun `when request is valid and the book exists, updateBookDetails should return the updated book and a 200 OK status`() =
        testApplication {
            val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                title = "Title",
                author = "Author",
                status = BookStatus.READ,
                genreIds = emptyList()
            )
            val updatedBookDto = BookMapperImpl(imageBaseUrl, GenreMapperImpl()).mapBookToDto(updatedBook)
            every { bookValidator.validateId(bookId.toString()) } returns bookId
            every { bookValidator.validateUpdate(bookUpdateDto) } returns bookDetailsUpdatePayload
            coEvery { updateBookDetailsUseCase.invoke(bookId, bookDetailsUpdatePayload, language) } returns updatedBook

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                val json = Json.encodeToString(bookUpdateDto)
                setBody(json)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(updatedBookDto, Json.decodeFromString<BookDto>(response.bodyAsText()))
        }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        coEvery { updateBookDetailsUseCase.invoke(any(), any(), any()) } returns updatedBook

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        client.put(url) {
            contentType(ContentType.Application.Json)
            val json = Json.encodeToString(bookUpdateDto)
            setBody(json)
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
        }

        coVerify(exactly = 1) { updateBookDetailsUseCase.invoke(any(), any(), "en") }
    }

    @Test
    fun `when validateId is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        every { bookValidator.validateId(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.put(url) {
            contentType(ContentType.Application.Json)
            val json = Json.encodeToString(bookUpdateDto)
            setBody(json)
        }

        Assertions.assertEquals(HttpStatusCode.InternalServerError, response.status)
        Assertions.assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        verify(exactly = 0) { bookValidator.validateUpdate(any()) }
        coVerify(exactly = 0) { updateBookDetailsUseCase.invoke(any(), any(), any()) }
    }

    @Test
    fun `when validateUpdate is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            every { bookValidator.validateUpdate(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                val json = Json.encodeToString(bookUpdateDto)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
            coVerify(exactly = 0) { updateBookDetailsUseCase.invoke(any(), any(), any()) }
        }

    @Test
    fun `when updateBookDetailsUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { updateBookDetailsUseCase.invoke(any(), any(), any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.put(url) {
                contentType(ContentType.Application.Json)
                val json = Json.encodeToString(bookUpdateDto)
                setBody(json)
            }

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }
}