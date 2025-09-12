package ru.jerael.booktracker.backend.domain.usecases.book

import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator

class AddBookUseCase(
    private val bookRepository: BookRepository,
    private val genresValidator: GenresValidator,
    private val coverStorage: CoverStorage
) {
    suspend operator fun invoke(
        payload: BookCreationPayload,
        coverBytes: ByteArray?,
        coverFileName: String?,
        language: String
    ): Book {
        genresValidator.invoke(payload.genreIds, language)
        val coverPath = if (coverBytes != null && coverFileName != null) {
            coverStorage.save(coverBytes, coverFileName)
        } else {
            null
        }
        val finalPayload = payload.copy(coverPath = coverPath)
        return bookRepository.addBook(finalPayload, language)
    }
}