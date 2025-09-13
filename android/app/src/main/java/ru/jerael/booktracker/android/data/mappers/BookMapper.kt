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

interface BookMapper {
    fun mapEntityToBook(entity: BookEntity): Book
    fun mapEntitiesToBooks(entities: List<BookEntity>): List<Book>
    fun mapDtoToEntity(dto: BookDto): BookEntity
    fun mapDtosToEntities(dtos: List<BookDto>): List<BookEntity>
    fun mapBookWithGenresToBook(bookWithGenres: BookWithGenres): Book
    fun mapBooksWithGenresToBooks(booksWithGenres: List<BookWithGenres>): List<Book>
}