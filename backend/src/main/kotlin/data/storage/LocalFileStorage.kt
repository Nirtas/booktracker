package ru.jerael.booktracker.backend.data.storage

import io.ktor.server.application.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.File

class LocalFileStorage(private val environment: ApplicationEnvironment) : FileStorage {

    private val storagePath = environment.config.property("ktor.storage.persistentPath").getString()

    override suspend fun saveFile(path: String, channel: ByteReadChannel): String {
        withContext(Dispatchers.IO) {
            val file = File(storagePath, path)
            file.parentFile.mkdirs()
            val writeChannel = file.writeChannel()
            channel.copyTo(writeChannel)
        }
        return path
    }

    override suspend fun deleteFile(path: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                File(storagePath, path).delete()
            } catch (e: Exception) {
                environment.log.error("Failed to delete file at path: $path")
                false
            }
        }
    }
}