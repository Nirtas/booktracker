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

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator

class AddBookUseCase(
    private val bookRepository: BookRepository,
    private val genresValidator: GenresValidator,
    private val coverStorage: CoverStorage
) {
    suspend operator fun invoke(
        payload: BookCreationPayload,
        coverBytes: ByteArray?,
        coverFileName: String?,
        language: String
    ): Book {
        genresValidator.invoke(payload.genreIds, language)
        val coverPath = if (coverBytes != null && coverFileName != null) {
            coverStorage.save(coverBytes, coverFileName)
        } else {
            null
        }
        val finalPayload = payload.copy(coverPath = coverPath)
        return bookRepository.addBook(finalPayload, language)
    }
}