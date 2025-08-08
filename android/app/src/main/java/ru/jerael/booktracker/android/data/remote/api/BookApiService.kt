package ru.jerael.booktracker.android.data.remote.api

import ru.jerael.booktracker.android.data.remote.dto.BookDto

interface BookApiService {
    suspend fun getBooks(): List<BookDto>
}