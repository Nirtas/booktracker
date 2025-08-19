package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.jerael.booktracker.android.data.local.dao.GenreDao
import ru.jerael.booktracker.android.data.mappers.toGenre
import ru.jerael.booktracker.android.data.mappers.toGenreEntity
import ru.jerael.booktracker.android.data.remote.api.GenreApiService
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.repository.GenreRepository
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val dao: GenreDao,
    private val api: GenreApiService
) : GenreRepository {
    override fun getGenres(): Flow<List<Genre>> {
        return dao.getAll().map { entities -> entities.map { it.toGenre() } }
    }

    override suspend fun refreshGenres(): Result<Unit> {
        return runCatching {
            val genreDtos = api.getGenres()
            val genreEntities = genreDtos.map { it.toGenreEntity() }
            dao.clearAndInsert(genreEntities)
        }
    }
}