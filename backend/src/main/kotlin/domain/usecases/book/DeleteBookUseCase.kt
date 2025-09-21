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
import ru.jerael.booktracker.backend.domain.exceptions.ExternalServiceException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.util.*

class DeleteBookUseCase(
    private val bookRepository: BookRepository,
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(id: UUID) {
        val bookToDelete = bookRepository.getBookById(id, "en") ?: throw BookNotFoundException(id.toString())
        bookToDelete.coverPath?.let { coverPath ->
            try {
                fileStorage.deleteFile(coverPath)
            } catch (e: Exception) {
                throw StorageException(
                    userMessage = "Couldn't delete the book cover. Please try again later.",
                    message = "Failed to delete file '$coverPath'. Reason: ${e.message}"
                )
            }
        }
        try {
            bookRepository.deleteBook(id)
        } catch (e: Exception) {
            throw ExternalServiceException(
                userMessage = "An error occurred while deleting the book from the database. Please try again later.",
                message = "Failed to delete book with id '$id'. Reason: ${e.message}"
            )
        }
    }
}