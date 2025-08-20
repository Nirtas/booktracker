package ru.jerael.booktracker.backend.domain.usecases.book

import io.ktor.http.content.*
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import java.util.*

class UpdateBookCoverUseCase(
    private val bookRepository: BookRepository,
    private val coverStorage: CoverStorage
) {
    suspend operator fun invoke(id: UUID, coverPart: PartData.FileItem): Book? {
        val existingBook = bookRepository.getBookById(id) ?: return null
        existingBook.coverPath?.let { coverStorage.delete(it) }
        val newCoverPath = coverStorage.save(coverPart)
        return bookRepository.updateBookCover(id, newCoverPath)
    }
}