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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.validation.codes.BookValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.validator.validateStatus
import ru.jerael.booktracker.backend.domain.model.book.BookStatus

class ValidateStatusTest {

    @Test
    fun `when status is valid, validateStatus should return an empty list`() {
        val validStatus = BookStatus.READ.value

        val errors = validateStatus(validStatus)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `when status is invalid, validateStatus should return INVALID_STATUS error`() {
        val invalidStatus = "non_existent_status"

        val errors = validateStatus(invalidStatus)

        assertEquals(1, errors.size)
        assertEquals(BookValidationErrorCode.INVALID_STATUS, errors[0].code)
    }
}