package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.mappers.toGenre
import ru.jerael.booktracker.backend.domain.exceptions.GenreNotFoundException
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class GenreRepositoryImpl : GenreRepository {
    override suspend fun getGenres(language: String): List<Genre> {
        return withContext(Dispatchers.IO) {
            transaction {
                val nameColumn = if (language.startsWith("ru")) Genres.nameRu else Genres.nameEn
                Genres.select(Genres.id, nameColumn).orderBy(nameColumn).map { it.toGenre(nameColumn) }
            }
        }
    }

    override suspend fun getGenreById(id: Int, language: String): Genre {
        return withContext(Dispatchers.IO) {
            transaction {
                val nameColumn = if (language.startsWith("ru")) Genres.nameRu else Genres.nameEn
                Genres
                    .select(Genres.id, nameColumn)
                    .where { Genres.id eq id }
                    .orderBy(nameColumn)
                    .map { it.toGenre(nameColumn) }
                    .singleOrNull() ?: throw GenreNotFoundException(id)
            }
        }
    }

    override suspend fun getGenresByIds(ids: List<Int>, language: String): List<Genre> {
        return withContext(Dispatchers.IO) {
            transaction {
                val nameColumn = if (language.startsWith("ru")) Genres.nameRu else Genres.nameEn
                Genres
                    .select(Genres.id, nameColumn)
                    .where { Genres.id inList ids }
                    .orderBy(nameColumn)
                    .map { it.toGenre(nameColumn) }
            }
        }
    }
}