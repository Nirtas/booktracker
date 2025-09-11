package ru.jerael.booktracker.android.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import ru.jerael.booktracker.android.data.local.dao.GenreDao
import ru.jerael.booktracker.android.data.mappers.GenreMapper
import ru.jerael.booktracker.android.data.remote.api.GenreApiService
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.repository.GenreRepository
import javax.inject.Inject

class GenreRepositoryImpl @Inject constructor(
    private val dao: GenreDao,
    private val api: GenreApiService,
    private val errorMapper: ErrorMapper,
    private val genreMapper: GenreMapper
) : GenreRepository {
    override fun getGenres(): Flow<Result<List<Genre>>> {
        return dao.getAll()
            .map { entities -> appSuccess(genreMapper.mapEntitiesToGenres(entities)) }
            .catch { emit(appFailure(it, errorMapper)) }
    }

    override suspend fun refreshGenres(): Result<Unit> {
        return try {
            val genreDtos = api.getGenres()
            val genreEntities = genreMapper.mapDtosToEntities(genreDtos)
            dao.clearAndInsert(genreEntities)
            appSuccess(Unit)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }
}