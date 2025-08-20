package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

abstract class AppException(
    val httpStatusCode: HttpStatusCode,
    message: String,
    val userMessage: String,
    val errorCode: String
) : RuntimeException(message)