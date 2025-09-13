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

package ru.jerael.booktracker.backend.api.validation

import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.util.*

class BookValidator {
    fun validateId(id: String?): UUID {
        if (id == null) throw ValidationException("Book ID is missing")
        return try {
            UUID.fromString(id)
        } catch (e: Exception) {
            throw ValidationException("Invalid Book ID format: '$id' is not a valid UUID.")
        }
    }

    fun validateCreation(dto: BookCreationDto): BookCreationPayload {
        if (dto.title.isBlank()) throw ValidationException("Book title can't be empty.")
        if (dto.title.length > 500) throw ValidationException("Book title can't be longer than 500 characters.")
        if (dto.author.isBlank()) throw ValidationException("Book author can't be empty.")
        if (dto.author.length > 500) throw ValidationException("Book author can't be longer than 500 characters.")
        val status = BookStatus.fromString(dto.status)
            ?: throw ValidationException("Invalid status: ${dto.status}. Allowed values are: ${BookStatus.entries.joinToString()}")

        return BookCreationPayload(
            title = dto.title,
            author = dto.author,
            coverPath = null,
            status = status,
            genreIds = dto.genreIds
        )
    }

    fun validateUpdate(dto: BookUpdateDto): BookDetailsUpdatePayload {
        if (dto.title.isBlank()) throw ValidationException("Book title can't be empty.")
        if (dto.title.length > 500) throw ValidationException("Book title can't be longer than 500 characters.")
        if (dto.author.isBlank()) throw ValidationException("Book author can't be empty.")
        if (dto.author.length > 500) throw ValidationException("Book author can't be longer than 500 characters.")
        val status = BookStatus.fromString(dto.status)
            ?: throw ValidationException("Invalid status: ${dto.status}. Allowed values are: ${BookStatus.entries.joinToString()}")

        return BookDetailsUpdatePayload(
            title = dto.title,
            author = dto.author,
            status = status,
            genreIds = dto.genreIds
        )
    }
}