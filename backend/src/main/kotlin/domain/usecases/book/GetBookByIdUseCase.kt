package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*

class GetBookByIdUseCase(private val bookRepository: BookRepository) {
    suspend operator fun invoke(id: UUID): Book? {
        return bookRepository.getBookById(id)
    }
}