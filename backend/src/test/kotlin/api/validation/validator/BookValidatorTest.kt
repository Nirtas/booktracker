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

package api.validation.validator

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import kotlin.test.assertTrue

class BookValidatorTest {

    private val validator: BookValidator = BookValidator()
    private val bookCreationDto = BookCreationDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )
    private val bookUpdateDto = BookUpdateDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )

    @Test
    fun `when dto is valid, validateCreation shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateCreation(bookCreationDto)
        }
    }

    @Test
    fun `when title is invalid, validateCreation should throw ValidationException`() {
        val invalidDto = bookCreationDto.copy(title = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
    }

    @Test
    fun `when multiple fields are invalid, validateCreation should throw ValidationException containing all errors`() {
        val invalidDto = bookCreationDto.copy(title = "", author = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
        assertTrue(exception.errors.containsKey("author"))
    }

    @Test
    fun `when dto is valid, validateUpdate shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateUpdate(bookUpdateDto)
        }
    }

    @Test
    fun `when title is invalid, validateUpdate should throw ValidationException`() {
        val invalidDto = bookUpdateDto.copy(title = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdate(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
    }

    @Test
    fun `when multiple fields are invalid, validateUpdate should throw ValidationException containing all errors`() {
        val invalidDto = bookUpdateDto.copy(title = "", author = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdate(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
        assertTrue(exception.errors.containsKey("author"))
    }
}