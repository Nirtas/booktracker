package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import java.util.*

class UpdateBookDetailsUseCase(
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository,
    private val getBookByIdUseCase: GetBookByIdUseCase
) {
    suspend operator fun invoke(id: UUID, payload: BookDetailsUpdatePayload): Book {
        getBookByIdUseCase(id)
        val uniqueGenres = payload.genreIds.distinct()
        if (uniqueGenres.isNotEmpty()) {
            val foundGenres = genreRepository.getGenresByIds(uniqueGenres)
            if (foundGenres.count() != uniqueGenres.count()) {
                val notFoundGenreIds = uniqueGenres.toSet() - foundGenres.map { it.id }.toSet()
                throw ValidationException("One or more genres not found: ${notFoundGenreIds.joinToString()}")
            }
        }
        return bookRepository.updateBookDetails(id, payload)
    }
}