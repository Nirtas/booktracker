package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

open class NotFoundException(
    userMessage: String,
    errorCode: String = "RESOURCE_NOT_FOUND"
) : AppException(
    httpStatusCode = HttpStatusCode.NotFound,
    message = userMessage,
    userMessage = userMessage,
    errorCode = errorCode
)

class BookNotFoundException(bookId: String) : NotFoundException(
    userMessage = "Book with ID '$bookId' was not found.",
    errorCode = "BOOK_NOT_FOUND"
)

class GenreNotFoundException(genreId: Int) : NotFoundException(
    userMessage = "Genre with ID '$genreId' was not found.",
    errorCode = "GENRE_NOT_FOUND"
)