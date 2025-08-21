package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDataPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import java.util.*

interface BookRepository {
    suspend fun getBooks(language: String): List<Book>
    suspend fun addBook(bookDataPayload: BookDataPayload, language: String): Book
    suspend fun getBookById(id: UUID, language: String): Book?
    suspend fun updateBookDetails(id: UUID, bookDetailsUpdatePayload: BookDetailsUpdatePayload, language: String): Book
    suspend fun updateBookCover(id: UUID, newCoverPath: String, language: String): Book
    suspend fun deleteBook(id: UUID): Boolean
}