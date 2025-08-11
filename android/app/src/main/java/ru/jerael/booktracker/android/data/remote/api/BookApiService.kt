package ru.jerael.booktracker.android.data.remote.api

import ru.jerael.booktracker.android.data.remote.dto.BookCreationDto
import ru.jerael.booktracker.android.data.remote.dto.BookDto
import java.io.File

interface BookApiService {
    suspend fun getBooks(): List<BookDto>

    suspend fun addBook(bookCreationDto: BookCreationDto, coverFile: File?): BookDto
}