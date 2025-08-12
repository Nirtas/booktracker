package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import java.util.*

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun addBook(bookCreationPayload: BookCreationPayload): Book
    suspend fun getBookById(id: UUID): Book?
}