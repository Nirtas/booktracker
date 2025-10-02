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

package ru.jerael.booktracker.backend.api.parsing

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.domain.validation.ValidationError
import ru.jerael.booktracker.backend.domain.validation.ValidationException
import ru.jerael.booktracker.backend.domain.validation.codes.CommonValidationErrorCode

data class ParsedBookCreationRequest(
    val bookCreationDto: BookCreationDto,
    val coverBytes: ByteArray?,
    val coverFileName: String?
)

data class ParsedBookCoverUpdateRequest(
    val coverBytes: ByteArray,
    val coverFileName: String
)

class MultipartParser {
    suspend fun parseBookCreation(call: ApplicationCall): ParsedBookCreationRequest {
        var bookCreationDto: BookCreationDto? = null
        var coverBytes: ByteArray? = null
        var coverFileName: String? = null

        val multipartData = call.receiveMultipart()
        multipartData.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name == "book") {
                        bookCreationDto = Json.decodeFromString(part.value)
                    }
                }

                is PartData.FileItem -> {
                    if (part.name == "cover" && part.originalFileName?.isNotBlank() == true) {
                        coverFileName = part.originalFileName
                        coverBytes = part.provider().readRemaining().readByteArray()
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        val errors = mutableMapOf<String, List<ValidationError>>()
        if (bookCreationDto == null) {
            val error = ValidationError(CommonValidationErrorCode.INVALID_FORM_ITEM)
            errors["book"] = listOf(error)
        }
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }

        return ParsedBookCreationRequest(
            bookCreationDto = bookCreationDto!!,
            coverBytes = coverBytes,
            coverFileName = coverFileName
        )
    }

    suspend fun parseBookCoverUpdate(call: ApplicationCall): ParsedBookCoverUpdateRequest {
        val part = call.receiveMultipart().readPart()
        try {
            if (part !is PartData.FileItem || part.name != "cover" || part.originalFileName.isNullOrBlank()) {
                val error = mapOf("cover" to listOf(ValidationError(CommonValidationErrorCode.INVALID_FILE_PART)))
                throw ValidationException(error)
            }
            val coverBytes: ByteArray = part.provider().readRemaining().readByteArray()
            val coverFileName: String = part.originalFileName!!
            return ParsedBookCoverUpdateRequest(coverBytes = coverBytes, coverFileName = coverFileName)
        } finally {
            part?.dispose?.let { it() }
        }
    }
}