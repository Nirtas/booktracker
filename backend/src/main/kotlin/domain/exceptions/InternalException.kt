package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

class InternalException(
    userMessage: String = "An internal server error occurred. Please try again later.",
    message: String
) : AppException(
    httpStatusCode = HttpStatusCode.InternalServerError,
    userMessage = userMessage,
    message = message,
    errorCode = "INTERNAL_SERVER_ERROR"
)