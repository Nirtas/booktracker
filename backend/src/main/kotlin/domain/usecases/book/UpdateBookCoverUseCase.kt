package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import java.util.*

class UpdateBookCoverUseCase(
    private val bookRepository: BookRepository,
    private val coverStorage: CoverStorage,
    private val getBookByIdUseCase: GetBookByIdUseCase
) {
    suspend operator fun invoke(
        id: UUID,
        coverBytes: ByteArray,
        coverFileName: String,
        language: String
    ): Book {
        val existingBook = getBookByIdUseCase(id, language)
        existingBook.coverPath?.let { coverStorage.delete(it) }
        val newCoverPath = coverStorage.save(coverBytes, coverFileName)
        return bookRepository.updateBookCover(id, newCoverPath, language)
    }
}