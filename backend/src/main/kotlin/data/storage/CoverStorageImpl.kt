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

import io.ktor.utils.io.*
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.File
import java.util.*

private const val COVERS_PATH_PREFIX = "covers"

class CoverStorageImpl(
    private val fileStorage: FileStorage
) : CoverStorage {
    override suspend fun save(content: ByteArray, fileName: String): String {
        if (fileName.isBlank()) {
            throw ValidationException("File name can`t be empty.")
        }
        val fileExtension = File(fileName).extension
        if (fileExtension !in listOf("jpg", "jpeg", "png")) {
            throw ValidationException("Invalid file type. Only JPG and PNG are allowed.")
        }
        val path = "$COVERS_PATH_PREFIX/${UUID.randomUUID()}.$fileExtension"
        val channel = ByteReadChannel(content)
        fileStorage.saveFile(path, channel)
        return path
    }

    override suspend fun delete(path: String) {
        return fileStorage.deleteFile(path)
    }
}