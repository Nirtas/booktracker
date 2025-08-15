package ru.jerael.booktracker.android.domain.usecases

import android.net.Uri
import android.util.Log
import ru.jerael.booktracker.android.domain.model.BookCreationPayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(title: String, author: String, coverUri: Uri?): Result<String> {
        return try {
            val coverFile: File? = coverUri?.let { fileStorage.saveFile(it) }
            val bookCreationPayload = BookCreationPayload(
                title = title,
                author = author,
                coverFile = coverFile
            )
            repository.addBook(bookCreationPayload)
        } catch (e: Exception) {
            Log.e("UpdateBookUseCase", "Ошибка при обновлении файла", e)
            Result.failure(e)
        }
    }
}