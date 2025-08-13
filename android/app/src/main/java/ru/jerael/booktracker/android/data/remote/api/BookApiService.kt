package ru.jerael.booktracker.android.data.remote.api

import ru.jerael.booktracker.android.data.remote.dto.BookCreationDto
import ru.jerael.booktracker.android.data.remote.dto.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.BookDto
import java.io.File

interface BookApiService {
    suspend fun getBooks(): List<BookDto>

    suspend fun addBook(bookCreationDto: BookCreationDto, coverFile: File?): BookDto

    suspend fun getBookById(id: String): BookDto

    suspend fun updateBook(
        id: String,
        bookDetailsUpdateDto: BookDetailsUpdateDto,
        coverFile: File?
    ): BookDto
}