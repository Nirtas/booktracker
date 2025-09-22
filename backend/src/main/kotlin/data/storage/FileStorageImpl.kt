/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.backend.data.storage

import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.jerael.booktracker.backend.domain.exceptions.AppException
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileStorageImpl(
    private val storagePath: String,
    private val logger: Logger
) : FileStorage {
    override suspend fun saveFile(path: String, inputStream: InputStream): String {
        val file = File(storagePath, path)
        try {
            withContext(Dispatchers.IO) {
                file.parentFile.mkdirs()
                var bytesCopied: Long
                inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        bytesCopied = input.copyTo(output)
                    }
                }
                if (bytesCopied == 0L) {
                    file.delete()
                    throw IOException("File content can't be empty.")
                }
            }
        } catch (e: Exception) {
            if (file.exists()) {
                file.delete()
            }
            if (e is AppException) throw e
            logger.error("Failed to save file to path: $path", e)
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
            logger.error("Failed to delete file at path: $path", e)
            throw StorageException(message = "Failed to delete file '$path'. Reason: ${e.message}")
        }
    }
}