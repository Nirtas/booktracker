package ru.jerael.booktracker.android.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.jerael.booktracker.android.data.remote.HttpRoute
import ru.jerael.booktracker.android.data.remote.dto.BookDto

class BookApiServiceImpl(private val httpClient: HttpClient) : BookApiService {
    override suspend fun getBooks(): List<BookDto> {
        return httpClient.get(HttpRoute.BOOKS).body<List<BookDto>>()
    }
}