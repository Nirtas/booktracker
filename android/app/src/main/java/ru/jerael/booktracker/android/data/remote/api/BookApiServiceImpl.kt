package ru.jerael.booktracker.android.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.appendPathSegments
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.android.data.remote.HttpRoute
import ru.jerael.booktracker.android.data.remote.dto.BookCreationDto
import ru.jerael.booktracker.android.data.remote.dto.BookDto
import java.io.File

class BookApiServiceImpl(private val httpClient: HttpClient) : BookApiService {
    override suspend fun getBooks(): List<BookDto> {
        return httpClient.get(HttpRoute.BOOKS).body<List<BookDto>>()
    }

    override suspend fun addBook(bookCreationDto: BookCreationDto, coverFile: File?): BookDto {
        val bookJson = Json.encodeToString(bookCreationDto)
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
        return httpClient.get(HttpRoute.BOOKS) {
            url {
                appendPathSegments(id)
            }
        }.body<BookDto>()
    }
}