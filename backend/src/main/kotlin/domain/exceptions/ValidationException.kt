package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

class ValidationException(
    userMessage: String
) : AppException(
    httpStatusCode = HttpStatusCode.BadRequest,
    message = userMessage,
    userMessage = userMessage,
    errorCode = "VALIDATION_ERROR"
)