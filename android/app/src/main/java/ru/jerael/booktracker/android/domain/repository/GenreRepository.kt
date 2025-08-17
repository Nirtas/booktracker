package ru.jerael.booktracker.android.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.domain.model.genre.Genre

interface GenreRepository {
    fun getGenres(): Flow<List<Genre>>
    suspend fun refreshGenres(): Result<Unit>
}