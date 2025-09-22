/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.mappers.toGenre
import ru.jerael.booktracker.backend.domain.exceptions.GenresNotFoundException
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
                    .singleOrNull() ?: throw GenresNotFoundException(listOf(id))
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