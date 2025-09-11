package ru.jerael.booktracker.android.data.remote.api

import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsCreationDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDetailsUpdateDto
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import java.io.File

interface BookApiService {
    suspend fun getBooks(): List<BookDto>

    suspend fun addBook(bookDetailsCreationDto: BookDetailsCreationDto, coverFile: File?): BookDto

    suspend fun getBookById(id: String): BookDto

    suspend fun updateBookDetails(id: String, bookDetailsUpdateDto: BookDetailsUpdateDto): BookDto

    suspend fun updateBookCover(id: String, coverFile: File): BookDto

    suspend fun deleteBook(id: String)
}