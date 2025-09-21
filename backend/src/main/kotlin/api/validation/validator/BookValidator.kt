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

package ru.jerael.booktracker.backend.api.validation.validator

import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.util.putIfNotEmpty
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.BookValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.domain.model.book.BookStatus

class BookValidator {
    companion object {
        private const val MAX_TITLE_LENGTH = 500
        private const val MAX_AUTHOR_LENGTH = 500
    }

    fun validateCreation(dto: BookCreationDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("title", validateTitle(dto.title))
        errors.putIfNotEmpty("author", validateAuthor(dto.author))
        errors.putIfNotEmpty("status", validateStatus(dto.status))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    fun validateUpdate(dto: BookUpdateDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("title", validateTitle(dto.title))
        errors.putIfNotEmpty("author", validateAuthor(dto.author))
        errors.putIfNotEmpty("status", validateStatus(dto.status))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    private fun validateTitle(title: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (title.isBlank()) {
            errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
        } else {
            if (title.length > MAX_TITLE_LENGTH) {
                errors.add(
                    ValidationError(
                        code = CommonValidationErrorCode.FIELD_TOO_LONG,
                        params = mapOf("max" to listOf(MAX_TITLE_LENGTH.toString()))
                    )
                )
            }
        }
        return errors
    }

    private fun validateAuthor(author: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (author.isBlank()) {
            errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
        } else {
            if (author.length > MAX_AUTHOR_LENGTH) {
                errors.add(
                    ValidationError(
                        code = CommonValidationErrorCode.FIELD_TOO_LONG,
                        params = mapOf("max" to listOf(MAX_AUTHOR_LENGTH.toString()))
                    )
                )
            }
        }
        return errors
    }

    private fun validateStatus(status: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (BookStatus.fromString(status) == null) {
            val allowedStatuses = BookStatus.entries.map { it.value }
            val error = ValidationError(
                code = BookValidationErrorCode.INVALID_STATUS,
                params = mapOf("allowed" to allowedStatuses)
            )
            errors.add(error)
        }
        return errors
    }
}