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

package ru.jerael.booktracker.backend.domain.validation.validator

import ru.jerael.booktracker.backend.domain.exceptions.EmptyFileContentException
import ru.jerael.booktracker.backend.domain.exceptions.EmptyFileNameException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidFileExtensionException

class CoverValidator {
    operator fun invoke(coverBytes: ByteArray, coverFileName: String) {
        if (coverFileName.isBlank()) {
            throw EmptyFileNameException()
        }
        val fileExtension = coverFileName.substringAfterLast('.', "").lowercase()
        val allowedExtensions = listOf("jpg", "jpeg", "png")
        if (fileExtension !in allowedExtensions) {
            throw InvalidFileExtensionException(allowedExtensions)
        }
        if (coverBytes.isEmpty()) {
            throw EmptyFileContentException()
        }
    }
}