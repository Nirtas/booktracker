package ru.jerael.booktracker.android.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.get
import ru.jerael.booktracker.android.data.remote.HttpRoute
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto

class GenreApiServiceImpl(private val httpClient: HttpClient) : GenreApiService {
    override suspend fun getGenres(): List<GenreDto> {
        return httpClient.get(HttpRoute.GENRES) { expectSuccess = true }.body<List<GenreDto>>()
    }
}