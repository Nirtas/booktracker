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

package ru.jerael.booktracker.backend.api.error_handlers

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.domain.exceptions.*

fun StatusPagesConfig.configureDomainExceptions() {
    exception<AlreadyExistsException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.Conflict, errorDto)
    }
    exception<CredentialsException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        val statusCode = when (cause) {
            is PasswordVerificationException -> HttpStatusCode.BadRequest
            is InvalidCredentialsException -> HttpStatusCode.Unauthorized
            is InvalidRefreshTokenException -> HttpStatusCode.Forbidden
            is ExpiredRefreshTokenException -> HttpStatusCode.Forbidden
            else -> HttpStatusCode.BadRequest
        }
        call.respond(statusCode, errorDto)
    }
    exception<ExternalServiceException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.ServiceUnavailable, errorDto)
    }
    exception<FileValidationException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.BadRequest, errorDto)
    }
    exception<ForbiddenException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.Forbidden, errorDto)
    }
    exception<InternalException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.InternalServerError, errorDto)
    }
    exception<NotFoundException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.NotFound, errorDto)
    }
    exception<StorageException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.InternalServerError, errorDto)
    }
    exception<UnauthenticatedException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.Unauthorized, errorDto)
    }
    exception<VerificationException> { call, cause ->
        val errorDto = createErrorDto(cause.errorCode, cause.userMessage)
        call.respond(HttpStatusCode.BadRequest, errorDto)
    }
}

private fun createErrorDto(code: String, message: String) = ErrorDto(code = code, message = message)