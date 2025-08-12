package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.mappers.toBook
import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository

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
}