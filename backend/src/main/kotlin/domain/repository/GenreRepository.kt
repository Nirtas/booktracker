package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.genre.Genre

interface GenreRepository {
    suspend fun getGenres(): List<Genre>
}