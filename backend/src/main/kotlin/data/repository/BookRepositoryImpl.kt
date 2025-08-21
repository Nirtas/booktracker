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
import ru.jerael.booktracker.backend.data.mappers.toGenre
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDataPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class BookRepositoryImpl : BookRepository {
    override suspend fun getBooks(language: String): List<Book> {
        return withContext(Dispatchers.IO) {
            findBooks(language = language)
        }
    }

    override suspend fun addBook(bookDataPayload: BookDataPayload, language: String): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val result = Books.insert {
                    it[title] = bookDataPayload.title
                    it[author] = bookDataPayload.author
                    it[coverPath] = bookDataPayload.coverPath
                    it[status] = bookDataPayload.status
                }
                val newBookId = result[Books.id]
                bookDataPayload.genres.forEach { genre ->
                    BookGenres.insert {
                        it[bookId] = newBookId
                        it[genreId] = genre.id
                    }
                }
                findBooks(wherePredicate = { Books.id eq newBookId }, language = language).singleOrNull()
                    ?: throw InternalException(message = "Book with ID $newBookId was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun getBookById(id: UUID, language: String): Book? {
        return withContext(Dispatchers.IO) {
            findBooks(wherePredicate = { Books.id eq id }, language = language).singleOrNull()
        }
    }

    override suspend fun updateBookDetails(
        id: UUID,
        bookDetailsUpdatePayload: BookDetailsUpdatePayload,
        language: String
    ): Book {
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
                findBooks(wherePredicate = { Books.id eq id }, language = language).singleOrNull()
                    ?: throw InternalException(message = "Book with ID $id was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun updateBookCover(id: UUID, newCoverPath: String, language: String): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ Books.id eq id }) {
                    it[coverPath] = newCoverPath
                }
                if (updatedRows == 0) {
                    throw BookNotFoundException(id.toString())
                }
                findBooks(wherePredicate = { Books.id eq id }, language = language).singleOrNull()
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
                    coverPath = book[Books.coverPath],
                    status = book[Books.status],
                    createdAt = book[Books.createdAt].toInstant(),
                    genres = genres
                )
            }
        }
    }
}