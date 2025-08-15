package ru.jerael.booktracker.backend.data.storage

import io.ktor.http.content.*
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.File
import java.util.*

private const val COVERS_PATH_PREFIX = "covers"

class CoverStorageImpl(
    private val fileStorage: FileStorage
) : CoverStorage {
    override suspend fun save(filePart: PartData.FileItem): String {
        val fileExtension = File(filePart.originalFileName as String).extension
        val path = "$COVERS_PATH_PREFIX/${UUID.randomUUID()}.$fileExtension"
        val channel = filePart.provider()
        fileStorage.saveFile(path, channel)
        return path
    }

    override suspend fun delete(path: String) {
        return fileStorage.deleteFile(path)
    }
}