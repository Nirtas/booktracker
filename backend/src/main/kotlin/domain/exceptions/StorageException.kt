package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

class StorageException(
    userMessage: String = "A storage error occurred. Please try again later.",
    message: String
) : AppException(
    httpStatusCode = HttpStatusCode.InternalServerError,
    message = message,
    userMessage = userMessage,
    errorCode = "STORAGE_ERROR"
)