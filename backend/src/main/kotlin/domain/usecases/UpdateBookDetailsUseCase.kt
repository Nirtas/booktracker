package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class UpdateBookDetailsUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(id: UUID, bookDetailsUpdatePayload: BookDetailsUpdatePayload): Book? {
        return bookRepository.updateBookDetails(id, bookDetailsUpdatePayload)
    }
}