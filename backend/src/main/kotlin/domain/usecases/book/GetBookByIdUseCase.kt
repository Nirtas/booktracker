package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class GetBookByIdUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(id: UUID, language: String): Book {
        return bookRepository.getBookById(id, language) ?: throw BookNotFoundException(id.toString())
    }
}