package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDataPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import java.util.*

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun addBook(bookDataPayload: BookDataPayload): Book
    suspend fun getBookById(id: UUID): Book?
    suspend fun updateBookDetails(id: UUID, bookDetailsUpdatePayload: BookDetailsUpdatePayload): Book
    suspend fun updateBookCover(id: UUID, newCoverPath: String): Book
    suspend fun deleteBook(id: UUID): Boolean
}