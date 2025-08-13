package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookCoverUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class UpdateBookCoverUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(id: UUID, bookCoverUpdatePayload: BookCoverUpdatePayload): Book? {
        return bookRepository.updateBookCover(id, bookCoverUpdatePayload)
    }
}