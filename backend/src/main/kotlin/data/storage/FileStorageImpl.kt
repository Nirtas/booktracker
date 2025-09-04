package ru.jerael.booktracker.backend.data.storage

import io.ktor.server.application.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.jerael.booktracker.backend.domain.exceptions.AppException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.File
import java.io.FileOutputStream

class FileStorageImpl(private val environment: ApplicationEnvironment) : FileStorage {

    private val storagePath = environment.config.property("ktor.storage.persistentPath").getString()

    override suspend fun saveFile(path: String, channel: ByteReadChannel): String {
        val file = File(storagePath, path)
        try {
            withContext(Dispatchers.IO) {
                file.parentFile.mkdirs()
                var bytesCopied = 0L
                channel.toInputStream().use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        bytesCopied = inputStream.copyTo(outputStream)
                    }
                }
                if (bytesCopied == 0L) {
                    file.delete()
                    throw ValidationException("Uploaded file can't be empty.")
                }
            }
        } catch (e: Exception) {
            if (file.exists()) {
                file.delete()
            }
            if (e is AppException) throw e
            environment.log.error("Failed to save file to path: $path", e)
            throw StorageException(message = "Failed to save file to '$path'. Reason: ${e.message}")
        }
        return path
    }

    override suspend fun deleteFile(path: String) {
        try {
            withContext(Dispatchers.IO) {
                val file = File(storagePath, path)
                if (file.exists()) {
                    val wasDeleted = file.delete()
                    if (!wasDeleted) {
                        throw StorageException(message = "Failed to delete file at path: $path")
                    }
                }
            }
        } catch (e: Exception) {
            if (e is AppException) throw e
            environment.log.error("Failed to delete file at path: $path", e)
            throw StorageException(message = "Failed to delete file '$path'. Reason: ${e.message}")
        }
    }
}