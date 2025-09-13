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

package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus

class BookMapperImpl(
    private val imageBaseUrl: String,
    private val genreMapper: GenreMapper
) : BookMapper {
    override fun mapBookToDto(book: Book): BookDto {
        return BookDto(
            id = book.id.toString(),
            title = book.title,
            author = book.author,
            coverUrl = book.coverPath?.let { "$imageBaseUrl/$it" },
            status = book.status.value,
            createdAt = book.createdAt.toEpochMilli(),
            genres = genreMapper.mapGenresToDtos(book.genres)
        )
    }

    override fun mapBooksToDtos(books: List<Book>): List<BookDto> {
        return books.map { mapBookToDto(it) }
    }

    override fun mapCreationDtoToCreationPayload(dto: BookCreationDto): BookCreationPayload {
        return BookCreationPayload(
            title = dto.title,
            author = dto.author,
            coverPath = null,
            status = BookStatus.fromString(dto.status)!!,
            genreIds = dto.genreIds
        )
    }

    override fun mapUpdateDtoToDetailsUpdatePayload(dto: BookUpdateDto): BookDetailsUpdatePayload {
        return BookDetailsUpdatePayload(
            title = dto.title,
            author = dto.author,
            status = BookStatus.fromString(dto.status)!!,
            genreIds = dto.genreIds
        )
    }
}