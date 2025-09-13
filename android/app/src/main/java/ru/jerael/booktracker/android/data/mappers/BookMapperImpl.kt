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

package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import java.time.Instant
import javax.inject.Inject

class BookMapperImpl @Inject constructor(
    private val genreMapper: GenreMapper
) : BookMapper {
    override fun mapEntityToBook(entity: BookEntity): Book {
        return Book(
            id = entity.id,
            title = entity.title,
            author = entity.author,
            coverUrl = entity.coverUrl,
            status = BookStatus.fromString(entity.status) ?: BookStatus.WANT_TO_READ,
            createdAt = Instant.ofEpochMilli(entity.createdAt),
            genres = emptyList()
        )
    }

    override fun mapEntitiesToBooks(entities: List<BookEntity>): List<Book> {
        return entities.map { mapEntityToBook(it) }
    }

    override fun mapDtoToEntity(dto: BookDto): BookEntity {
        return BookEntity(
            id = dto.id,
            title = dto.title,
            author = dto.author,
            coverUrl = dto.coverUrl,
            status = dto.status,
            createdAt = dto.createdAt
        )
    }

    override fun mapDtosToEntities(dtos: List<BookDto>): List<BookEntity> {
        return dtos.map { mapDtoToEntity(it) }
    }

    override fun mapBookWithGenresToBook(bookWithGenres: BookWithGenres): Book {
        return mapEntityToBook(bookWithGenres.book).copy(
            genres = genreMapper.mapEntitiesToGenres(bookWithGenres.genres)
        )
    }

    override fun mapBooksWithGenresToBooks(booksWithGenres: List<BookWithGenres>): List<Book> {
        return booksWithGenres.map { mapBookWithGenresToBook(it) }
    }
}