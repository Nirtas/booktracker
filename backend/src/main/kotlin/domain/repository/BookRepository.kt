package ru.jerael.booktracker.backend.domain.repository

import ru.jerael.booktracker.backend.domain.model.Book

interface BookRepository {
    suspend fun getBooks(): List<Book>
}