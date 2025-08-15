package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository

class AddBookUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(bookCreationPayload: BookCreationPayload): Book {
        return bookRepository.addBook(bookCreationPayload)
    }
}