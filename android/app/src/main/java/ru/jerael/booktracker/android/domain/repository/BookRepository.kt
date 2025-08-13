package ru.jerael.booktracker.android.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.domain.model.BookCreationPayload
import ru.jerael.booktracker.android.domain.model.BookUpdatePayload

interface BookRepository {
    fun getBooks(): Flow<List<Book>>

    suspend fun refreshBooks(): Result<Unit>

    suspend fun addBook(bookCreationPayload: BookCreationPayload): Result<String>

    fun getBookById(id: String): Flow<Book?>

    suspend fun refreshBookById(id: String): Result<Unit>

    suspend fun updateBook(bookUpdatePayload: BookUpdatePayload): Result<Unit>
}