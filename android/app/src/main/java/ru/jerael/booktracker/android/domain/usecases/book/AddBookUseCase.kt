package ru.jerael.booktracker.android.domain.usecases.book

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val fileStorage: FileStorage,
    private val errorMapper: ErrorMapper
) {
    suspend operator fun invoke(bookCreationParams: BookCreationParams): Result<String> {
        return try {
            val coverFileResult: Result<File?> = if (bookCreationParams.coverUri != null) {
                fileStorage.saveFile(bookCreationParams.coverUri)
            } else {
                appSuccess(null)
            }
            if (coverFileResult.isFailure) {
                return appFailure(
                    throwable = coverFileResult.exceptionOrNull()!!,
                    errorMapper = errorMapper
                )
            }
            val bookCreationPayload = BookCreationPayload(
                title = bookCreationParams.title,
                author = bookCreationParams.author,
                coverFile = coverFileResult.getOrNull(),
                status = bookCreationParams.status,
                genreIds = bookCreationParams.genreIds
            )
            repository.addBook(bookCreationPayload)
        } catch (e: Exception) {
            appFailure(e, errorMapper)
        }
    }
}