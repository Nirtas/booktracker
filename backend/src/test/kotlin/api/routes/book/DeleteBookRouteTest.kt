package api.routes.book

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import java.util.*

class DeleteBookRouteTest : BooksRouteTestBase() {

    private val bookId = UUID.randomUUID()
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )
    private val url = "/api/books/$bookId"

    @Test
    fun `when a book successfully deleted, deleteBook should return a 204 No Content status`() = testApplication {
        coEvery { bookValidator.validateId(bookId.toString()) } returns bookId
        coEvery { deleteBookUseCase.invoke(bookId) } just Runs

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.delete(url)

        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun `when validateId is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        coEvery { bookValidator.validateId(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.delete(url)

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { deleteBookUseCase.invoke(any()) }
    }

    @Test
    fun `when deleteBookUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { deleteBookUseCase.invoke(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.delete(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            val errorDto = ErrorDto(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred. Please try again later."
            )
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }
}