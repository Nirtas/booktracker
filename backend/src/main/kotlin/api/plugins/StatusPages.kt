package ru.jerael.booktracker.backend.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import ru.jerael.booktracker.backend.data.dto.ErrorDto
import ru.jerael.booktracker.backend.domain.exceptions.AppException
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<UnsupportedMediaTypeException> { call, cause ->
            val errorDto = ErrorDto(
                code = "UNSUPPORTED_MEDIA_TYPE",
                message = cause.message ?: "The content type of the request is not supported."
            )
            call.respond(HttpStatusCode.UnsupportedMediaType, errorDto)
        }

        exception<BadRequestException> { call, cause ->
            val message = cause.cause?.message ?: cause.message ?: "Invalid request format"
            val validationException = ValidationException(message)
            val errorDto = ErrorDto(
                code = validationException.errorCode,
                message = validationException.userMessage
            )
            call.respond(validationException.httpStatusCode, errorDto)
        }

        exception<SerializationException> { call, cause ->
            val message = when (cause) {
                is MissingFieldException -> "Request JSON is missing required field: '${cause.missingFields.joinToString()}'"
                else -> cause.message ?: "Invalid JSON format"
            }
            val validationException = ValidationException(message)
            val errorDto = ErrorDto(
                code = validationException.errorCode,
                message = validationException.userMessage
            )
            call.respond(validationException.httpStatusCode, errorDto)
        }

        exception<AppException> { call, cause ->
            val errorDto = ErrorDto(
                code = cause.errorCode,
                message = cause.userMessage
            )
            call.respond(cause.httpStatusCode, errorDto)
        }

        exception<Throwable> { call, cause ->
            this@configureStatusPages.log.error("An unexpected error occurred", cause)
            val errorDto = ErrorDto(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred. Please try again later."
            )
            call.respond(HttpStatusCode.InternalServerError, errorDto)
        }
    }
}
