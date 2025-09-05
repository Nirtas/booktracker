package api.parsing

import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Serializable
private data class BookCoverUpdateResultDto(
    val hasFile: Boolean,
    val fileName: String?
)

private const val COVER_UPDATE_URL = "/test-cover-update"

private fun Application.configureTestParserRouting(parser: MultipartParser) {
    routing {
        post(COVER_UPDATE_URL) {
            val fileItem = parser.parseBookCoverUpdate(call)
            val bookCoverUpdateResultDto = BookCoverUpdateResultDto(
                hasFile = true,
                fileName = fileItem.originalFileName
            )
            call.respond(HttpStatusCode.OK, bookCoverUpdateResultDto)
        }
    }
}

class ParseBookCoverUpdateTest {

    private val parser: MultipartParser = MultipartParser()

    @Test
    fun `when multipart contains a valid 'cover' file part, parseBookCoverUpdate should return the FileItem`() =
        testApplication {
            val coverFilename = "cover.jpg"

            application {
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(COVER_UPDATE_URL) {
                val multipartBody = MultiPartFormDataContent(
                    parts = formData {
                        append(
                            "cover",
                            "image content".toByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"$coverFilename\"")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val bookCoverUpdateResultDto = Json.decodeFromString<BookCoverUpdateResultDto>(response.bodyAsText())
            assertTrue(bookCoverUpdateResultDto.hasFile)
            assertEquals(coverFilename, bookCoverUpdateResultDto.fileName)
        }

    @Test
    fun `when multipart contains only 'book' part, an ValidationException should be thrown with 400 BadRequest status`() =
        testApplication {
            val bookCreationDto = BookCreationDto(
                title = "Title",
                author = "Author",
                status = BookStatus.READ.value,
                genreIds = emptyList()
            )

            application {
                configureStatusPages()
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(COVER_UPDATE_URL) {
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

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("File part 'cover' is missing or invalid"))
        }

    @Test
    fun `when the FileItem has an incorrect name (not 'cover'), an ValidationException should be thrown with 400 BadRequest status`() =
        testApplication {
            val coverFilename = "cover.jpg"

            application {
                configureStatusPages()
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(COVER_UPDATE_URL) {
                val multipartBody = MultiPartFormDataContent(
                    parts = formData {
                        append(
                            "invalid",
                            "image content".toByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"$coverFilename\"")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("File part 'cover' is missing or invalid"))
        }

    @Test
    fun `when the FileItem has a blank filename, an ValidationException should be thrown with 400 BadRequest status`() =
        testApplication {
            val coverFilename = ""

            application {
                configureStatusPages()
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(COVER_UPDATE_URL) {
                val multipartBody = MultiPartFormDataContent(
                    parts = formData {
                        append(
                            "cover",
                            "image content".toByteArray(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"$coverFilename\"")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("File part 'cover' is missing or invalid"))
        }

    @Test
    fun `when multipart is empty, an ValidationException should be thrown with 400 BadRequest status`() =
        testApplication {
            application {
                configureStatusPages()
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(COVER_UPDATE_URL) {
                val multipartBody = MultiPartFormDataContent(parts = formData {})
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertTrue(response.bodyAsText().contains("File part 'cover' is missing or invalid"))
        }
}
