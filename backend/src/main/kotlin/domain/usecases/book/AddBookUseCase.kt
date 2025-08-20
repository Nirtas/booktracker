package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDataPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class AddBookUseCase(
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(payload: BookCreationPayload): Book {
        val uniqueGenres = payload.genreIds.distinct()
        val genres = if (uniqueGenres.isEmpty()) {
            emptyList()
        } else {
            val foundGenres = genreRepository.getGenresByIds(uniqueGenres)
            if (foundGenres.count() != uniqueGenres.count()) {
                val notFoundGenreIds = uniqueGenres.toSet() - foundGenres.map { it.id }.toSet()
                throw ValidationException("One or more genres not found: ${notFoundGenreIds.joinToString()}")
            }
            foundGenres
        }
        val bookDataPayload = BookDataPayload(
            title = payload.title,
            author = payload.author,
            coverPath = payload.coverPath,
            status = payload.status,
            genres = genres
        )
        return bookRepository.addBook(bookDataPayload)
    }
}