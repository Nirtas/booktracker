package api.routes.book

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.slot
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
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
import kotlin.test.assertEquals

class UpdateBookCoverRouteTest : BooksRouteTestBase() {

    private val bookId = UUID.randomUUID()
    private val updatedBook = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverPath = null,
        status = BookStatus.READ,
        createdAt = Instant.now(),
        genres = emptyList()
    )
    private val updatedBookDto = BookMapperImpl(imageBaseUrl).toDto(updatedBook)
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )
    private val url = "/api/books/$bookId/cover"

    @Test
    fun `when request is valid with cover image, updateBookCover should return the updated book and a 200 OK status`() =
        testApplication {
            val coverPartSlot = slot<PartData.FileItem>()
            coEvery { updateBookCoverUseCase.invoke(any(), capture(coverPartSlot), any()) } returns updatedBook

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url) {
                val multipartBody = MultiPartFormDataContent(
                    parts = formData {
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

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(updatedBookDto, Json.decodeFromString<BookDto>(response.bodyAsText()))
            assertNotNull(coverPartSlot.captured)
        }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        coEvery { updateBookCoverUseCase.invoke(any(), any(), any()) } returns updatedBook

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        client.post(url) {
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
        }

        coVerify(exactly = 1) { updateBookCoverUseCase.invoke(any(), any(), "en") }
    }

    @Test
    fun `when validateId is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        every { bookValidator.validateId(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.post(url) {
            val multipartBody = MultiPartFormDataContent(
                parts = formData {
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

        Assertions.assertEquals(HttpStatusCode.InternalServerError, response.status)
        Assertions.assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { updateBookCoverUseCase.invoke(any(), any(), any()) }
    }

    @Test
    fun `when multipart parsing is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { multipartParser.parseBookCoverUpdate(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
            coVerify(exactly = 0) { updateBookCoverUseCase.invoke(any(), any(), any()) }
        }

    @Test
    fun `when updateBookCoverUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { updateBookCoverUseCase.invoke(any(), any(), any()) } throws Exception("Error")

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