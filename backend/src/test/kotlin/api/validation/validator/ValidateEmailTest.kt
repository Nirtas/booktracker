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
import ru.jerael.booktracker.backend.api.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.codes.EmailValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.validator.validateEmail

class ValidateEmailTest {

    @Test
    fun `when email is valid, validateEmail should return an empty list`() {
        val validEmail = "test@example.com"

        val errors = validateEmail(validEmail)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `when email is empty, validateEmail should return FIELD_CANNOT_BE_EMPTY error`() {
        val invalidEmail = ""

        val errors = validateEmail(invalidEmail)

        assertEquals(1, errors.size)
        assertEquals(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY, errors[0].code)
    }

    @Test
    fun `when email is too long, validateEmail should return FIELD_TOO_LONG error`() {
        val invalidEmail = "a".repeat(500) + "@test.com"

        val errors = validateEmail(invalidEmail)

        assertEquals(2, errors.size)
        assertEquals(CommonValidationErrorCode.FIELD_TOO_LONG, errors[0].code)
        assertEquals(EmailValidationErrorCode.INVALID_FORMAT, errors[1].code)
    }

    @Test
    fun `when email has invalid format, validateEmail should return INVALID_FORMAT error`() {
        val invalidEmail = "test.example.com"

        val errors = validateEmail(invalidEmail)

        assertEquals(1, errors.size)
        assertEquals(EmailValidationErrorCode.INVALID_FORMAT, errors[0].code)
    }
}