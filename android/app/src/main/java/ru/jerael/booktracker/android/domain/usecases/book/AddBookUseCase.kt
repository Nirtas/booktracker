package ru.jerael.booktracker.android.domain.usecases.book

import android.util.Log
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository,
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(bookCreationParams: BookCreationParams): Result<String> {
        return try {
            val coverFile: File? = bookCreationParams.coverUri?.let { fileStorage.saveFile(it) }
            val bookCreationPayload = BookCreationPayload(
                title = bookCreationParams.title,
                author = bookCreationParams.author,
                coverFile = coverFile,
                status = bookCreationParams.status,
                genreIds = bookCreationParams.genreIds
            )
            repository.addBook(bookCreationPayload)
        } catch (e: Exception) {
            Log.e("AddBookUseCase", "Ошибка при добавлении книги", e)
            Result.failure(e)
        }
    }
}