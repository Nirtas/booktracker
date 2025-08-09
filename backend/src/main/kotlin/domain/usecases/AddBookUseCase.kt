package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class AddBookUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(bookCreationPayload: BookCreationPayload): UUID {
        return bookRepository.addBook(bookCreationPayload)
    }
}