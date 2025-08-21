package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.genre.Genre

interface GenreRepository {
    suspend fun getGenres(language: String): List<Genre>
    suspend fun getGenreById(id: Int, language: String): Genre
    suspend fun getGenresByIds(ids: List<Int>, language: String): List<Genre>
}