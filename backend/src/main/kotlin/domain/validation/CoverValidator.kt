package ru.jerael.booktracker.backend.domain.validation

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