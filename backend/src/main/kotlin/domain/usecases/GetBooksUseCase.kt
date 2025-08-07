package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.domain.model.Book
import ru.jerael.booktracker.backend.domain.repository.BookRepository

class GetBooksUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(): List<Book> {
        return bookRepository.getBooks()
    }
}