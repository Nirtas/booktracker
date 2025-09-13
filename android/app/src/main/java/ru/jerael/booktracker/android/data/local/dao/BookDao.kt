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

package ru.jerael.booktracker.android.data.local.dao

import androidx.annotation.VisibleForTesting
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOKS
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOK_GENRES
import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres

@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM $TABLE_BOOKS")
    fun getBooksWithGenres(): Flow<List<BookWithGenres>>

    @Transaction
    @Query("SELECT * FROM $TABLE_BOOKS WHERE id = :id")
    fun getBookWithGenresById(id: String): Flow<BookWithGenres?>

    @VisibleForTesting
    @Query("SELECT * FROM $TABLE_BOOK_GENRES")
    fun getBookGenres(): List<BookGenresEntity>

    @VisibleForTesting
    @Query("SELECT * FROM $TABLE_BOOKS")
    fun getBooks(): List<BookEntity>

    @Upsert
    suspend fun upsertBook(bookEntity: BookEntity)

    @Upsert
    suspend fun upsertBookGenres(bookGenresEntities: List<BookGenresEntity>)

    @Upsert
    suspend fun upsertBookWithGenres(
        bookEntity: BookEntity,
        bookGenresEntities: List<BookGenresEntity>
    ) {
        upsertBook(bookEntity)
        deleteBookGenres(bookEntity.id)
        upsertBookGenres(bookGenresEntities)
    }

    @Query("DELETE FROM $TABLE_BOOKS")
    suspend fun clearBooks()

    @Transaction
    suspend fun clearAndInsertBooks(
        books: List<BookEntity>,
        genres: List<BookGenresEntity>
    ) {
        clearBooks()
        books.forEach { book ->
            val bookGenres = genres.filter { it.bookId == book.id }
            upsertBookWithGenres(book, bookGenres)
        }
    }

    @Query("DELETE FROM $TABLE_BOOK_GENRES WHERE book_id = :id")
    suspend fun deleteBookGenres(id: String)

    @Query("DELETE FROM $TABLE_BOOKS WHERE id = :id")
    suspend fun deleteBookById(id: String)
}
