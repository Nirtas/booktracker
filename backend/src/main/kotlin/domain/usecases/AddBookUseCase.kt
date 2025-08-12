package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository

class AddBookUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(bookCreationPayload: BookCreationPayload): Book {
        return bookRepository.addBook(bookCreationPayload)
    }
}