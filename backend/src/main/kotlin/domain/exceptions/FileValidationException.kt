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

package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

abstract class FileValidationException(
    userMessage: String,
    errorCode: String
) : AppException(
    httpStatusCode = HttpStatusCode.BadRequest,
    message = userMessage,
    userMessage = userMessage,
    errorCode = errorCode
)

class EmptyFileNameException : FileValidationException(
    userMessage = "File name can't be empty.",
    errorCode = "EMPTY_FILE_NAME"
)

class InvalidFileExtensionException(
    val allowedExtensions: List<String>
) : FileValidationException(
    userMessage = "Invalid file extensions. Allowed types: ${allowedExtensions.joinToString()}",
    errorCode = "INVALID_FILE_EXTENSION"
)

class EmptyFileContentException : FileValidationException(
    userMessage = "File content can't be empty.",
    errorCode = "EMPTY_FILE_CONTENT"
)