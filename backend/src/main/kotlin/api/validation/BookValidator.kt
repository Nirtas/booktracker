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