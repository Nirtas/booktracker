package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.mappers.toGenre
import ru.jerael.booktracker.backend.domain.exceptions.GenreNotFoundException
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class GenreRepositoryImpl : GenreRepository {
    override suspend fun getGenres(): List<Genre> {
        return withContext(Dispatchers.IO) {
            transaction {
                Genres.selectAll().map { it.toGenre() }
            }
        }
    }

    override suspend fun getGenreById(id: Int): Genre {
        return withContext(Dispatchers.IO) {
            transaction {
                Genres.selectAll().where { Genres.id eq id }.map { it.toGenre() }.singleOrNull()
                    ?: throw GenreNotFoundException(id)
            }
        }
    }

    override suspend fun getGenresByIds(ids: List<Int>): List<Genre> {
        return withContext(Dispatchers.IO) {
            transaction {
                Genres.selectAll().where { Genres.id inList ids }.map { it.toGenre() }
            }
        }
    }
}