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
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.BookGenres
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.mappers.toGenre
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.model.book.AddBookData
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookCoverData
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookDetailsData
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class BookRepositoryImpl : BookRepository {
    override suspend fun getBooks(userId: UUID, language: String): List<Book> {
        return withContext(Dispatchers.IO) {
            findBooks(wherePredicate = { Books.userId eq userId }, language = language)
        }
    }

    override suspend fun addBook(data: AddBookData, language: String): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val result = Books.insert {
                    it[userId] = data.userId
                    it[title] = data.title
                    it[author] = data.author
                    it[coverUrl] = data.coverUrl
                    it[status] = data.status
                }
                val newBookId = result[Books.id]
                data.genreIds.forEach { genreId ->
                    BookGenres.insert {
                        it[bookId] = newBookId
                        it[this.genreId] = genreId
                    }
                }
                findBooks(
                    wherePredicate = { (Books.userId eq data.userId) and (Books.id eq newBookId) },
                    language = language
                ).singleOrNull()
                    ?: throw InternalException(message = "Book with ID $newBookId was created but could not be found immediately after.")
            }
        }
    }

    override suspend fun getBookById(userId: UUID, bookId: UUID, language: String): Book? {
        return withContext(Dispatchers.IO) {
            findBooks(
                wherePredicate = { (Books.userId eq userId) and (Books.id eq bookId) },
                language = language
            ).singleOrNull()
        }
    }

    override suspend fun updateBookDetails(data: UpdateBookDetailsData, language: String): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ (Books.userId eq data.userId) and (Books.id eq data.bookId) }) {
                    it[title] = data.title
                    it[author] = data.author
                    it[status] = data.status
                }
                if (updatedRows == 0) {
                    throw BookNotFoundException(data.bookId.toString())
                }
                BookGenres.deleteWhere { BookGenres.bookId eq data.bookId }
                BookGenres.batchUpsert(data.genreIds) { genreId ->
                    this[BookGenres.bookId] = data.bookId
                    this[BookGenres.genreId] = genreId
                }
                findBooks(
                    wherePredicate = { (Books.userId eq data.userId) and (Books.id eq data.bookId) },
                    language = language
                ).singleOrNull()
                    ?: throw InternalException(message = "Book with ID ${data.bookId} was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun updateBookCover(data: UpdateBookCoverData, language: String): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ (Books.userId eq data.userId) and (Books.id eq data.bookId) }) {
                    it[coverUrl] = data.coverUrl
                }
                if (updatedRows == 0) {
                    throw BookNotFoundException(data.bookId.toString())
                }
                findBooks(
                    wherePredicate = { (Books.userId eq data.userId) and (Books.id eq data.bookId) },
                    language = language
                ).singleOrNull()
                    ?: throw InternalException(message = "Book with ID ${data.bookId} was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun deleteBook(userId: UUID, bookId: UUID) {
        withContext(Dispatchers.IO) {
            transaction {
                val deletedRows = Books.deleteWhere { (Books.userId eq userId) and (Books.id eq bookId) }
                if (deletedRows == 0) {
                    throw BookNotFoundException(bookId.toString())
                }
            }
        }
    }

    private fun findBooks(
        wherePredicate: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
        language: String
    ): List<Book> {
        return transaction {
            val nameColumn = if (language.startsWith("ru")) Genres.nameRu else Genres.nameEn
            val query = (Books leftJoin BookGenres leftJoin Genres).selectAll()
            wherePredicate?.let { query.where(it) }
            query.groupBy(
                keySelector = { it[Books.id] },
                valueTransform = { it }
            ).map { (bookId, rows) ->
                val book = rows.first()
                val genres = rows.mapNotNull {
                    val genreId = it.getOrNull(Genres.id)
                    val genreName = it.getOrNull(nameColumn)
                    if (genreId != null && genreName != null) {
                        it.toGenre(nameColumn)
                    } else {
                        null
                    }
                }
                Book(
                    id = bookId,
                    title = book[Books.title],
                    author = book[Books.author],
                    coverUrl = book[Books.coverUrl],
                    status = book[Books.status],
                    createdAt = book[Books.createdAt].toInstant(),
                    genres = genres
                )
            }
        }
    }
}