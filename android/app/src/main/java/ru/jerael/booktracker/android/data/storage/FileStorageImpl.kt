package ru.jerael.booktracker.android.data.storage

import android.app.Application
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class FileStorageImpl @Inject constructor(
    private val application: Application
) : FileStorage {
    override suspend fun saveFile(uri: Uri): File? {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = application.contentResolver
                val fileName = "cover_${System.currentTimeMillis()}.jpg"
                val file = File(application.cacheDir, fileName)
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                file
            } catch (e: Exception) {
                Log.e("FileStorageImpl", "Ошибка при сохранении файла", e)
                null
            }
        }
    }

}