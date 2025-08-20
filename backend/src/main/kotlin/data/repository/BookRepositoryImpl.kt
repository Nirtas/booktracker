package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.BookGenres
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDataPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class BookRepositoryImpl : BookRepository {
    override suspend fun getBooks(): List<Book> {
        return withContext(Dispatchers.IO) {
            findBooks()
        }
    }

    override suspend fun addBook(bookDataPayload: BookDataPayload): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val result = Books.insert {
                    it[title] = bookDataPayload.title
                    it[author] = bookDataPayload.author
                    it[coverPath] = bookDataPayload.coverPath
                    it[status] = bookDataPayload.status
                }
                bookDataPayload.genres.forEach { genre ->
                    BookGenres.insert {
                        it[bookId] = result[Books.id]
                        it[genreId] = genre.id
                    }
                }
                Book(
                    id = result[Books.id],
                    title = bookDataPayload.title,
                    author = bookDataPayload.author,
                    coverPath = bookDataPayload.coverPath,
                    status = bookDataPayload.status,
                    createdAt = result[Books.createdAt].toInstant(),
                    genres = bookDataPayload.genres
                )
            }
        }
    }

    override suspend fun getBookById(id: UUID): Book? {
        return withContext(Dispatchers.IO) {
            findBooks(wherePredicate = { Books.id eq id }).singleOrNull()
        }
    }

    override suspend fun updateBookDetails(id: UUID, bookDetailsUpdatePayload: BookDetailsUpdatePayload): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ Books.id eq id }) {
                    it[title] = bookDetailsUpdatePayload.title
                    it[author] = bookDetailsUpdatePayload.author
                    it[status] = bookDetailsUpdatePayload.status
                }
                if (updatedRows == 0) {
                    throw BookNotFoundException(id.toString())
                }
                BookGenres.deleteWhere { bookId eq id }
                BookGenres.batchUpsert(bookDetailsUpdatePayload.genreIds) { genreId ->
                    this[BookGenres.bookId] = id
                    this[BookGenres.genreId] = genreId
                }
                findBooks(wherePredicate = { Books.id eq id }).singleOrNull()
                    ?: throw InternalException(message = "Book with ID $id was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun updateBookCover(id: UUID, newCoverPath: String): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ Books.id eq id }) {
                    it[coverPath] = newCoverPath
                }
                if (updatedRows == 0) {
                    throw BookNotFoundException(id.toString())
                }
                findBooks(wherePredicate = { Books.id eq id }).singleOrNull()
                    ?: throw InternalException(message = "Book with ID $id was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun deleteBook(id: UUID): Boolean {
        return withContext(Dispatchers.IO) {
            transaction {
                val deletedRows = Books.deleteWhere { Books.id eq id }
                deletedRows > 0
            }
        }
    }

    private fun findBooks(wherePredicate: (SqlExpressionBuilder.() -> Op<Boolean>)? = null): List<Book> {
        return transaction {
            val query = (Books leftJoin BookGenres leftJoin Genres).selectAll()
            wherePredicate?.let { query.where(it) }
            query.groupBy(
                keySelector = { it[Books.id] },
                valueTransform = { it }
            ).map { (bookId, rows) ->
                val book = rows.first()
                val genres = rows.mapNotNull {
                    val genreId = it.getOrNull(Genres.id)
                    val genreName = it.getOrNull(Genres.name)
                    if (genreId != null && genreName != null) {
                        Genre(
                            id = it[Genres.id],
                            name = it[Genres.name]
                        )
                    } else {
                        null
                    }
                }
                Book(
                    id = bookId,
                    title = book[Books.title],
                    author = book[Books.author],
                    coverPath = book[Books.coverPath],
                    status = book[Books.status],
                    createdAt = book[Books.createdAt].toInstant(),
                    genres = genres
                )
            }
        }
    }
}