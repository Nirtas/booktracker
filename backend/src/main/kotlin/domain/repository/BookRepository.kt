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

package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.book.AddBookData
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookCoverData
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookDetailsData
import java.util.*

interface BookRepository {
    suspend fun getBooks(userId: UUID, language: String): List<Book>
    suspend fun addBook(data: AddBookData, language: String): Book
    suspend fun getBookById(userId: UUID, bookId: UUID, language: String): Book?
    suspend fun updateBookDetails(data: UpdateBookDetailsData, language: String): Book
    suspend fun updateBookCover(data: UpdateBookCoverData, language: String): Book
    suspend fun deleteBook(userId: UUID, bookId: UUID)
}