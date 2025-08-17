package ru.jerael.booktracker.android.domain.usecases.book

import android.util.Log
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.model.book.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(bookUpdateParams: BookUpdateParams): Result<Unit> {
        return try {
            val coverFile: File? = bookUpdateParams.coverUri?.let { fileStorage.saveFile(it) }
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
            Log.e("UpdateBookUseCase", "Ошибка при обновлении книги", e)
            Result.failure(e)
        }
    }
}