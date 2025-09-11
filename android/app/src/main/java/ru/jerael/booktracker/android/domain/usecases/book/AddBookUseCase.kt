package ru.jerael.booktracker.android.domain.usecases.book

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val saveCoverFileUseCase: SaveCoverFileUseCase,
    private val errorMapper: ErrorMapper
) {
    suspend operator fun invoke(bookCreationParams: BookCreationParams): Result<String> {
        return try {
            val coverFile = saveCoverFileUseCase(bookCreationParams.coverUri).getOrThrow()
            val bookCreationPayload = BookCreationPayload(
                title = bookCreationParams.title,
                author = bookCreationParams.author,
                coverFile = coverFile,
                status = bookCreationParams.status,
                genreIds = bookCreationParams.genreIds
            )
            repository.addBook(bookCreationPayload)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }
}