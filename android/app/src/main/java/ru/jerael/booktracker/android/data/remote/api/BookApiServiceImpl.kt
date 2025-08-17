package ru.jerael.booktracker.android.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.android.data.remote.HttpRoute
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsCreationDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import java.io.File

class BookApiServiceImpl(private val httpClient: HttpClient) : BookApiService {
    override suspend fun getBooks(): List<BookDto> {
        return httpClient.get(HttpRoute.BOOKS).body<List<BookDto>>()
    }

    override suspend fun addBook(
        bookDetailsCreationDto: BookDetailsCreationDto,
        coverFile: File?
    ): BookDto {
        val bookJson = Json.encodeToString(bookDetailsCreationDto)
        return httpClient.post(HttpRoute.BOOKS) {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("book", bookJson, Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        })
                        coverFile?.let { file ->
                            append("cover", file.readBytes(), Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                            })
                        }
                    }
                ))
        }.body()
    }

    override suspend fun getBookById(id: String): BookDto {
        return httpClient.get(HttpRoute.bookById(id)).body<BookDto>()
    }

    override suspend fun updateBook(
        id: String,
        bookDetailsUpdateDto: BookDetailsUpdateDto,
        coverFile: File?
    ): BookDto {
        var bookDto = httpClient.put(HttpRoute.bookById(id)) {
            contentType(ContentType.Application.Json)
            setBody(bookDetailsUpdateDto)
        }.body<BookDto>()
        if (coverFile != null) {
            bookDto = httpClient.post(HttpRoute.bookCover(id)) {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("cover", coverFile.readBytes(), Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "filename=\"${coverFile.name}\""
                                )
                            })
                        }
                    )
                )
            }.body<BookDto>()
        }
        return bookDto
    }

    override suspend fun deleteBook(id: String) {
        httpClient.delete(HttpRoute.bookById(id))
    }
}