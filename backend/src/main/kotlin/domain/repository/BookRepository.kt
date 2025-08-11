package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload

interface BookRepository {
    suspend fun getBooks(): List<Book>
    suspend fun addBook(bookCreationPayload: BookCreationPayload): Book
}