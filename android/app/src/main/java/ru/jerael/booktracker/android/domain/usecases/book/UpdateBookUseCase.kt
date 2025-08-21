package ru.jerael.booktracker.android.domain.usecases.book

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val fileStorage: FileStorage,
    private val errorMapper: ErrorMapper
) {
    suspend operator fun invoke(bookUpdateParams: BookUpdateParams): Result<Unit> {
        return try {
            val coverFileResult: Result<File?> = if (bookUpdateParams.coverUri != null) {
                fileStorage.saveFile(bookUpdateParams.coverUri)
            } else {
                appSuccess(null)
            }
            if (coverFileResult.isFailure) {
                return appFailure(
                    throwable = coverFileResult.exceptionOrNull()!!,
                    errorMapper = errorMapper
                )
            }
            val bookUpdatePayload = BookUpdatePayload(
                id = bookUpdateParams.id,
                title = bookUpdateParams.title,
                author = bookUpdateParams.author,
                coverFile = coverFileResult.getOrNull(),
                status = bookUpdateParams.status,
                genreIds = bookUpdateParams.genreIds
            )
            repository.updateBook(bookUpdatePayload)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }
}