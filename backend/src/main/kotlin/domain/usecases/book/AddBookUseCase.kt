package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDataPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class AddBookUseCase(
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository
) {
    suspend operator fun invoke(bookCreationPayload: BookCreationPayload): Book {
        val genres = bookCreationPayload.genreIds.mapNotNull {
            genreRepository.getGenreById(it)
        }.toList()
        if (genres.count() != bookCreationPayload.genreIds.count()) {
            throw Exception("One or more genres not found")
        }
        val bookDataPayload = BookDataPayload(
            title = bookCreationPayload.title,
            author = bookCreationPayload.author,
            coverPath = bookCreationPayload.coverPath,
            status = bookCreationPayload.status,
            genres = genres
        )
        return bookRepository.addBook(bookDataPayload)
    }
}