package ru.jerael.booktracker.backend.api.parsing

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException

data class ParsedBookCreationRequest(
    val bookCreationDto: BookCreationDto,
    val coverPart: PartData.FileItem?
)

class MultipartParser {
    suspend fun parseBookCreation(call: ApplicationCall): ParsedBookCreationRequest {
        var bookCreationDto: BookCreationDto? = null
        var coverPart: PartData.FileItem? = null

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
                        coverPart = part
                        return@forEachPart
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        return ParsedBookCreationRequest(
            bookCreationDto = bookCreationDto
                ?: throw ValidationException("Form item 'book' is missing or has invalid format."),
            coverPart = coverPart
        )
    }

    suspend fun parseBookCoverUpdate(call: ApplicationCall): PartData.FileItem {
        val part = call.receiveMultipart().readPart()
        if (part !is PartData.FileItem || part.name != "cover" || part.originalFileName.isNullOrBlank()) {
            part?.dispose?.let { it() }
            throw ValidationException("File part 'cover' is missing or invalid")
        }
        return part
    }
}