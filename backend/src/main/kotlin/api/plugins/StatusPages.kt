/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.backend.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerializationException
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.validation.ValidationErrorDto
import ru.jerael.booktracker.backend.api.dto.validation.ValidationErrorParams
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.domain.exceptions.AppException

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

        exception<ValidationException> { call, cause ->
            val errorDetails = cause.errors.mapValues { entry ->
                entry.value.map { validationError ->
                    ValidationErrorParams(
                        code = validationError.code.name,
                        params = validationError.params
                    )
                }
            }
            val errorDto = ValidationErrorDto(
                message = cause.userMessage,
                details = errorDetails
            )
            call.respond(cause.httpStatusCode, errorDto)
        }

        exception<SerializationException> { call, cause ->
            val message = when (cause) {
                is MissingFieldException -> "Request JSON is missing required field: '${cause.missingFields.joinToString()}'"
                else -> cause.message ?: "Invalid JSON format"
            }
            val errorDto = ErrorDto(
                code = "BAD_REQUEST",
                message = message
            )
            call.respond(HttpStatusCode.BadRequest, errorDto)
        }

        exception<BadRequestException> { call, _ ->
            val errorDto = ErrorDto(
                code = "BAD_REQUEST",
                message = "Invalid request body or parameters."
            )
            call.respond(HttpStatusCode.BadRequest, errorDto)
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
