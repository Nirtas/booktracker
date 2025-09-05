package api.routes.book

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.time.Instant
import java.util.*

class GetBookByIdRouteTest : BooksRouteTestBase() {

    private val language = "en"
    private val bookId = UUID.randomUUID()
    private val expectedBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )
    private val url = "/api/books/$bookId"

    @Test
    fun `when a book is found, getBookById should return it and a 200 OK status`() = testApplication {
        val expectedBookDto = BookMapperImpl(imageBaseUrl).toDto(expectedBook)
        every { bookValidator.validateId(bookId.toString()) } returns bookId
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns expectedBook

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.get(url)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expectedBookDto, Json.decodeFromString<BookDto>(response.bodyAsText()))
    }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        coEvery { getBookByIdUseCase.invoke(bookId, language) } returns expectedBook

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        client.get(url) {
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
        }

        coVerify(exactly = 1) { getBookByIdUseCase.invoke(any(), any()) }
    }

    @Test
    fun `when validateId is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        every { bookValidator.validateId(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.get(url)

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { getBookByIdUseCase.invoke(any(), any()) }
    }

    @Test
    fun `when getBookByIdUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { getBookByIdUseCase.invoke(any(), any()) } throws Exception("Error")

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