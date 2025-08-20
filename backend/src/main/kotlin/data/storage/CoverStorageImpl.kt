package ru.jerael.booktracker.backend.data.storage

import io.ktor.http.content.*
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.File
import java.util.*

private const val COVERS_PATH_PREFIX = "covers"

class CoverStorageImpl(
    private val fileStorage: FileStorage
) : CoverStorage {
    override suspend fun save(filePart: PartData.FileItem): String {
        val originalFileName = filePart.originalFileName
        if (originalFileName.isNullOrBlank()) {
            throw ValidationException("File name can`t be empty.")
        }
        val fileExtension = File(originalFileName).extension
        if (fileExtension !in listOf("jpg", "jpeg", "png")) {
            throw ValidationException("Invalid file type. Only JPG and PNG are allowed.")
        }
        val path = "$COVERS_PATH_PREFIX/${UUID.randomUUID()}.$fileExtension"
        val channel = filePart.provider()
        fileStorage.saveFile(path, channel)
        return path
    }

    override suspend fun delete(path: String) {
        return fileStorage.deleteFile(path)
    }
}