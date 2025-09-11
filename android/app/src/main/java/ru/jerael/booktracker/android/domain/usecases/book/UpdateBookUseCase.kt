package ru.jerael.booktracker.android.domain.usecases.book

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val saveCoverFileUseCase: SaveCoverFileUseCase,
    private val errorMapper: ErrorMapper
) {
    suspend operator fun invoke(bookUpdateParams: BookUpdateParams): Result<Unit> {
        return try {
            val coverFile = saveCoverFileUseCase(bookUpdateParams.coverUri).getOrThrow()
            val bookUpdatePayload = BookUpdatePayload(
                id = bookUpdateParams.id,
                title = bookUpdateParams.title,
                author = bookUpdateParams.author,
                coverFile = coverFile,
                status = bookUpdateParams.status,
                genreIds = bookUpdateParams.genreIds
            )
            repository.updateBook(bookUpdatePayload)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }
}