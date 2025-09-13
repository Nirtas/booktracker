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

package ru.jerael.booktracker.android.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload

interface BookRepository {
    fun getBooks(): Flow<Result<List<Book>>>

    suspend fun refreshBooks(): Result<Unit>

    suspend fun addBook(bookCreationPayload: BookCreationPayload): Result<String>

    fun getBookById(id: String): Flow<Result<Book>>

    suspend fun refreshBookById(id: String): Result<Unit>

    suspend fun updateBook(bookUpdatePayload: BookUpdatePayload): Result<Unit>

    suspend fun deleteBook(id: String): Result<Unit>
}