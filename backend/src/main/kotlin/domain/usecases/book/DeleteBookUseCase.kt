package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.util.*

class DeleteBookUseCase(
    private val bookRepository: BookRepository,
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(id: UUID): Boolean {
        val bookToDelete = bookRepository.getBookById(id) ?: return false
        val wasDetailsDeleted = bookRepository.deleteBook(id)
        if (!wasDetailsDeleted) {
            return false
        }
        bookToDelete.coverPath?.let {
            fileStorage.deleteFile(it)
        }
        return true
    }
}