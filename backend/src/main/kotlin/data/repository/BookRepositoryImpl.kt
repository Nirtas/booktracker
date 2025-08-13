package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.mappers.toBook
import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookCoverUpdatePayload
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class BookRepositoryImpl : BookRepository {
    override suspend fun getBooks(): List<Book> {
        return withContext(Dispatchers.IO) {
            transaction {
                Books.selectAll().map { it.toBook() }
            }
        }
    }

    override suspend fun addBook(bookCreationPayload: BookCreationPayload): Book {
        return withContext(Dispatchers.IO) {
            transaction {
                val result = Books.insert {
                    it[title] = bookCreationPayload.title
                    it[author] = bookCreationPayload.author
                    it[coverPath] = bookCreationPayload.coverPath
                }
                Book(
                    id = result[Books.id],
                    title = bookCreationPayload.title,
                    author = bookCreationPayload.author,
                    coverPath = bookCreationPayload.coverPath
                )
            }
        }
    }

    override suspend fun getBookById(id: UUID): Book? {
        return withContext(Dispatchers.IO) {
            transaction {
                Books.selectAll().where(Books.id eq id).singleOrNull()?.toBook()
            }
        }
    }

    override suspend fun updateBookDetails(id: UUID, bookDetailsUpdatePayload: BookDetailsUpdatePayload): Book? {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ Books.id eq id }) {
                    it[title] = bookDetailsUpdatePayload.title
                    it[author] = bookDetailsUpdatePayload.author
                }
                if (updatedRows > 0) {
                    Books.selectAll().where(Books.id eq id).singleOrNull()?.toBook()
                } else {
                    null
                }
            }
        }
    }

    override suspend fun updateBookCover(id: UUID, bookCoverUpdatePayload: BookCoverUpdatePayload): Book? {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Books.update({ Books.id eq id }) {
                    it[coverPath] = bookCoverUpdatePayload.coverPath
                }
                if (updatedRows > 0) {
                    Books.selectAll().where(Books.id eq id).singleOrNull()?.toBook()
                } else {
                    null
                }
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
}