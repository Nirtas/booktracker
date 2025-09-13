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
import org.junit.jupiter.api.assertNull
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Serializable
private data class BookCreationResultDto(
    val bookCreationDto: BookCreationDto,
    val hasCover: Boolean,
    val coverFilename: String?
)

private const val CREATION_URL = "/test-creation"

private fun Application.configureTestParserRouting(parser: MultipartParser) {
    routing {
        post(CREATION_URL) {
            val result = parser.parseBookCreation(call)
            val bookCreationResultDto = BookCreationResultDto(
                bookCreationDto = result.bookCreationDto,
                hasCover = result.coverBytes != null,
                coverFilename = result.coverFileName
            )
            call.respond(HttpStatusCode.OK, bookCreationResultDto)
        }
    }
}

class ParseBookCreationTest {

    private val parser: MultipartParser = MultipartParser()

    @Test
    fun `when multipart contains 'book' and 'cover' parts, parseBookCreation should return a complete request object`() =
        testApplication {
            val bookCreationDto = BookCreationDto(
                title = "Title",
                author = "Author",
                status = BookStatus.READ.value,
                genreIds = emptyList()
            )
            val coverFilename = "cover.jpg"

            application {
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(CREATION_URL) {
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
                                append(HttpHeaders.ContentDisposition, "filename=\"$coverFilename\"")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val bookCreationResultDto = Json.decodeFromString<BookCreationResultDto>(response.bodyAsText())
            assertEquals(bookCreationDto, bookCreationResultDto.bookCreationDto)
            assertTrue(bookCreationResultDto.hasCover)
            assertEquals(coverFilename, bookCreationResultDto.coverFilename)
        }

    @Test
    fun `when multipart is missing the 'book' part, an ValidationException should be thrown with 400 BadRequest status`() =
        testApplication {
            val coverFilename = "cover.jpg"

            application {
                configureStatusPages()
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(CREATION_URL) {
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
            assertTrue(response.bodyAsText().contains("Form item 'book' is missing or has invalid format."))
        }

    @Test
    fun `when multipart contains only 'book' part, parseBookCreation should return a request object with a null coverPart`() =
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
            val response = client.post(CREATION_URL) {
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

            assertEquals(HttpStatusCode.OK, response.status)
            val bookCreationResultDto = Json.decodeFromString<BookCreationResultDto>(response.bodyAsText())
            assertEquals(bookCreationDto, bookCreationResultDto.bookCreationDto)
            assertFalse(bookCreationResultDto.hasCover)
            assertNull(bookCreationResultDto.coverFilename)
        }

    @Test
    fun `when multipart contains 'book' and 'cover' with a blank filename, parseBookCreation should return a request object with a null coverPart`() =
        testApplication {
            val bookCreationDto = BookCreationDto(
                title = "Title",
                author = "Author",
                status = BookStatus.READ.value,
                genreIds = emptyList()
            )
            val coverFilename = ""

            application {
                configureSerialization()
                configureTestParserRouting(parser)
            }
            val response = client.post(CREATION_URL) {
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
                                append(HttpHeaders.ContentDisposition, "filename=\"$coverFilename\"")
                            }
                        )
                    }
                )
                setBody(multipartBody)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val bookCreationResultDto = Json.decodeFromString<BookCreationResultDto>(response.bodyAsText())
            assertEquals(bookCreationDto, bookCreationResultDto.bookCreationDto)
            assertFalse(bookCreationResultDto.hasCover)
            assertNull(bookCreationResultDto.coverFilename)
        }
}
