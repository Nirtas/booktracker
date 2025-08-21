package ru.jerael.booktracker.android.data.storage

import android.app.Application
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class FileStorageImpl @Inject constructor(
    private val application: Application
) : FileStorage {
    override suspend fun saveFile(uri: Uri): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = application.contentResolver
                val fileName = "cover_${System.currentTimeMillis()}.jpg"
                val file = File(application.cacheDir, fileName)
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                } ?: return@withContext appFailure(AppError.FileStorageError)
                Result.success(file)
            } catch (e: Exception) {
                appFailure(AppError.FileStorageError)
            }
        }
    }
}