package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator
import java.util.*

class UpdateBookDetailsUseCase(
    private val bookRepository: BookRepository,
    private val genresValidator: GenresValidator,
    private val getBookByIdUseCase: GetBookByIdUseCase
) {
    suspend operator fun invoke(id: UUID, payload: BookDetailsUpdatePayload, language: String): Book {
        getBookByIdUseCase(id, language)
        genresValidator.invoke(payload.genreIds, language)
        return bookRepository.updateBookDetails(id, payload, language)
    }
}