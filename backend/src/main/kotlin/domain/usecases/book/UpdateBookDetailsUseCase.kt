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
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator
import java.util.*

class UpdateBookDetailsUseCase(
    private val bookRepository: BookRepository,
    private val genresValidator: GenresValidator,
    private val getBookByIdUseCase: GetBookByIdUseCase
) {
    suspend operator fun invoke(id: UUID, payload: BookDetailsUpdatePayload, language: String): Book {
        getBookByIdUseCase(id, language)
        genresValidator.invoke(payload.genreIds, language)
        return bookRepository.updateBookDetails(id, payload, language)
    }
}