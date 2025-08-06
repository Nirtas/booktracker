package ru.jerael.booktracker.android.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.domain.model.Book

interface BookRepository {
    fun getBooks(): Flow<List<Book>>
}