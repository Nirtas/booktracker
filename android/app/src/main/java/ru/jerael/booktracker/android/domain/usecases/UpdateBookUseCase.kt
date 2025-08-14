package ru.jerael.booktracker.android.domain.usecases

import android.net.Uri
import android.util.Log
import ru.jerael.booktracker.android.domain.model.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(
        id: String,
        title: String,
        author: String,
        coverUri: Uri?
    ): Result<Unit> {
        return try {
            val coverFile: File? = coverUri?.let { fileStorage.saveFile(it) }
            val bookUpdatePayload = BookUpdatePayload(
                id = id,
                title = title,
                author = author,
                coverFile = coverFile
            )
            repository.updateBook(bookUpdatePayload)
        } catch (e: Exception) {
            Log.e("UpdateBookUseCase", "Ошибка при обновлении файла", e)
            Result.failure(e)
        }
    }
}