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

package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCoverUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.UpdateBookCoverData
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.validation.validator.CoverValidator
import java.util.*

class UpdateBookCoverUseCase(
    private val bookRepository: BookRepository,
    private val coverStorage: CoverStorage,
    private val coverValidator: CoverValidator
) {
    suspend operator fun invoke(payload: BookCoverUpdatePayload): Book {
        val existingBook = bookRepository.getBookById(payload.userId, payload.bookId, payload.language)
            ?: throw BookNotFoundException(payload.bookId.toString())
        existingBook.coverUrl?.let { coverStorage.delete(it) }
        coverValidator(payload.coverBytes, payload.coverFileName)
        val fileExtension = payload.coverFileName.substringAfterLast('.', "")
        val path = "${payload.userId}/covers/${UUID.randomUUID()}.$fileExtension"
        val newCoverUrl = coverStorage.save(path, payload.coverBytes)
        val bookCoverUpdateData = UpdateBookCoverData(
            userId = payload.userId,
            bookId = payload.bookId,
            coverUrl = newCoverUrl
        )
        return bookRepository.updateBookCover(bookCoverUpdateData, payload.language)
    }
}