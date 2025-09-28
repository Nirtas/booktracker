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

package domain.validation

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.EmptyFileContentException
import ru.jerael.booktracker.backend.domain.exceptions.EmptyFileNameException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidFileExtensionException
import ru.jerael.booktracker.backend.domain.validation.CoverValidator

class CoverValidatorTest {

    private val validator: CoverValidator = CoverValidator()

    private val content = "file content".toByteArray()

    @Test
    fun `when cover is valid, validator should not throw any exception`() {
        assertDoesNotThrow {
            validator.invoke(content, "cover.jpg")
        }
        assertDoesNotThrow {
            validator.invoke(content, "cover.jpeg")
        }
        assertDoesNotThrow {
            validator.invoke(content, "cover.png")
        }
    }

    @Test
    fun `when file name is blank, an EmptyFileNameException should be thrown`() {
        val exception = assertThrows<EmptyFileNameException> {
            validator.invoke(content, " ")
        }
        assertEquals("EMPTY_FILE_NAME", exception.errorCode)
    }

    @Test
    fun `when file extension is unsupported, an InvalidFileExtensionException should be thrown`() {
        val exception = assertThrows<InvalidFileExtensionException> {
            validator.invoke(content, "document.gif")
        }
        assertEquals("INVALID_FILE_EXTENSION", exception.errorCode)
    }

    @Test
    fun `when file extension is in uppercase, validator should pass validation`() {
        assertDoesNotThrow {
            validator.invoke(content, "photo.JPG")
        }
        assertDoesNotThrow {
            validator.invoke(content, "IMAGE.PNG")
        }
    }

    @Test
    fun `when file name has no extension, an InvalidFileExtensionException should be thrown`() {
        assertThrows<InvalidFileExtensionException> {
            validator.invoke(content, "cover")
        }
    }

    @Test
    fun `when file content is empty, an EmptyFileContentException should be thrown`() {
        val emptyContent = byteArrayOf()
        val exception = assertThrows<EmptyFileContentException> {
            validator.invoke(emptyContent, "cover.jpg")
        }
        assertEquals("EMPTY_FILE_CONTENT", exception.errorCode)
    }
}