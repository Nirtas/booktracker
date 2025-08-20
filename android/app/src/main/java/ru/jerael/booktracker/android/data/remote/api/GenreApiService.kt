package ru.jerael.booktracker.android.data.remote.api

import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto

interface GenreApiService {
    suspend fun getGenres(): List<GenreDto>
}