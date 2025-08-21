package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.exceptions.ExternalServiceException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.util.*

class DeleteBookUseCase(
    private val bookRepository: BookRepository,
    private val fileStorage: FileStorage,
    private val getBookByIdUseCase: GetBookByIdUseCase
) {
    suspend operator fun invoke(id: UUID) {
        val bookToDelete = getBookByIdUseCase(id, "en")
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