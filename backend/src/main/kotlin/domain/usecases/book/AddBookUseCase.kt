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

import ru.jerael.booktracker.backend.domain.model.AssetType
import ru.jerael.booktracker.backend.domain.model.book.AddBookData
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.UserAssetStorage
import ru.jerael.booktracker.backend.domain.validation.validator.BookValidator
import ru.jerael.booktracker.backend.domain.validation.validator.CoverValidator
import ru.jerael.booktracker.backend.domain.validation.validator.GenreValidator

class AddBookUseCase(
    private val bookRepository: BookRepository,
    private val genreValidator: GenreValidator,
    private val userAssetStorage: UserAssetStorage,
    private val bookValidator: BookValidator,
    private val coverValidator: CoverValidator
) {
    suspend operator fun invoke(payload: BookCreationPayload): Book {
        bookValidator.validateCreation(payload)
        genreValidator.invoke(payload.genreIds, payload.language)
        val coverUrl = if (payload.coverBytes != null && payload.coverFileName != null) {
            coverValidator(payload.coverBytes, payload.coverFileName)
            userAssetStorage.save(
                userId = payload.userId,
                assetType = AssetType.COVER,
                fileName = payload.coverFileName,
                content = payload.coverBytes
            )
        } else {
            null
        }
        val addBookData = AddBookData(
            userId = payload.userId,
            title = payload.title,
            author = payload.author,
            coverUrl = coverUrl,
            status = payload.status,
            genreIds = payload.genreIds
        )
        return bookRepository.addBook(addBookData, payload.language)
    }
}