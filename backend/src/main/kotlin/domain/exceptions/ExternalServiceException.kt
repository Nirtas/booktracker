package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

class ExternalServiceException(
    userMessage: String,
    message: String
) : AppException(
    httpStatusCode = HttpStatusCode.ServiceUnavailable,
    message = message,
    userMessage = userMessage,
    errorCode = "EXTERNAL_SERVICE_ERROR"
)