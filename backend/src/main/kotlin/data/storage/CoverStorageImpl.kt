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

import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*

private const val COVERS_PATH_PREFIX = "covers"

class CoverStorageImpl(
    private val fileStorage: FileStorage
) : CoverStorage {
    override suspend fun save(content: ByteArray, fileName: String): String {
        val fileExtension = File(fileName).extension
        val path = "$COVERS_PATH_PREFIX/${UUID.randomUUID()}.$fileExtension"
        val inputStream = ByteArrayInputStream(content)
        fileStorage.saveFile(path, inputStream)
        return path
    }

    override suspend fun delete(path: String) {
        return fileStorage.deleteFile(path)
    }
}