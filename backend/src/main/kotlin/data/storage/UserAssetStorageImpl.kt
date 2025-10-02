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

import ru.jerael.booktracker.backend.domain.model.AssetType
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.storage.UserAssetStorage
import java.io.ByteArrayInputStream
import java.util.*

class UserAssetStorageImpl(
    private val fileStorage: FileStorage,
    private val imageBaseUrl: String
) : UserAssetStorage {
    override suspend fun save(userId: UUID, assetType: AssetType, fileName: String, content: ByteArray): String {
        val path = generatePath(userId, assetType, fileName)
        val inputStream = ByteArrayInputStream(content)
        fileStorage.saveFile(path, inputStream)
        return "$imageBaseUrl/$path"
    }

    override suspend fun delete(url: String) {
        val path = url.removePrefix("$imageBaseUrl/")
        fileStorage.deleteFile(path)
    }

    override suspend fun deleteUserDirectory(userId: UUID) {
        fileStorage.deleteDirectory(userId.toString())
    }

    private fun generatePath(userId: UUID, assetType: AssetType, fileName: String): String {
        val fileExtension = fileName.substringAfterLast('.', "")
        return "$userId/${assetType.directoryName}/${UUID.randomUUID()}.$fileExtension"
    }
}