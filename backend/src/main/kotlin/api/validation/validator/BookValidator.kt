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

class BookValidator {
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
}