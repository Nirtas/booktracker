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
import ru.jerael.booktracker.backend.api.dto.validation.ValidationErrorDto
import ru.jerael.booktracker.backend.api.dto.validation.ValidationErrorParams
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.FileValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.codes.GenreValidationErrorCode
import ru.jerael.booktracker.backend.domain.exceptions.GenresNotFoundException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidFileExtensionException

fun StatusPagesConfig.configureValidationExceptions() {
    exception<InvalidFileExtensionException> { _, cause ->
        val error = ValidationError(
            code = FileValidationErrorCode.INVALID_EXTENSION,
            params = mapOf("allowed" to cause.allowedExtensions)
        )
        throw ValidationException(mapOf("fileName" to listOf(error)))
    }
    exception<GenresNotFoundException> { _, cause ->
        val notFoundGenreIds = cause.genreIds.map { it.toString() }
        val error = ValidationError(
            code = GenreValidationErrorCode.NOT_FOUND,
            params = mapOf("notFound" to notFoundGenreIds)
        )
        throw ValidationException(mapOf("genres" to listOf(error)))
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
        call.respond(HttpStatusCode.UnprocessableEntity, errorDto)
    }
}