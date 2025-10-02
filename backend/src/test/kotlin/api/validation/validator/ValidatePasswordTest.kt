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
import ru.jerael.booktracker.backend.domain.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.domain.validation.codes.PasswordValidationErrorCode
import ru.jerael.booktracker.backend.domain.validation.validator.validatePassword

class ValidatePasswordTest {

    @Test
    fun `when password is valid, validatePassword should return an empty list`() {
        val validPassword = "Password123!"

        val errors = validatePassword(validPassword)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `when password is empty, validatePassword should return FIELD_CANNOT_BE_EMPTY error`() {
        val invalidPassword = ""

        val errors = validatePassword(invalidPassword)

        assertEquals(1, errors.size)
        assertEquals(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY, errors[0].code)
    }

    @Test
    fun `when password is too short, validatePassword should return LENGTH_INVALID error`() {
        val invalidPassword = "Pass1!"

        val errors = validatePassword(invalidPassword)

        assertEquals(1, errors.size)
        assertEquals(PasswordValidationErrorCode.LENGTH_INVALID, errors[0].code)
    }

    @Test
    fun `when password is missing a lowercase letter, validatePassword should return NEEDS_LOWERCASE error`() {
        val invalidPassword = "PASSWORD123!"

        val errors = validatePassword(invalidPassword)

        assertEquals(1, errors.size)
        assertEquals(PasswordValidationErrorCode.NEEDS_LOWERCASE, errors[0].code)
    }

    @Test
    fun `when password is missing an uppercase letter, validatePassword should return NEEDS_UPPERCASE error`() {
        val invalidPassword = "password123!"

        val errors = validatePassword(invalidPassword)

        assertEquals(1, errors.size)
        assertEquals(PasswordValidationErrorCode.NEEDS_UPPERCASE, errors[0].code)
    }

    @Test
    fun `when password is missing a digit, validatePassword should return NEEDS_DIGIT error`() {
        val invalidPassword = "Password!"

        val errors = validatePassword(invalidPassword)

        assertEquals(1, errors.size)
        assertEquals(PasswordValidationErrorCode.NEEDS_DIGIT, errors[0].code)
    }

    @Test
    fun `when password is missing a special character, validatePassword should return NEEDS_SPECIAL_CHAR error`() {
        val invalidPassword = "Password123"

        val errors = validatePassword(invalidPassword)

        assertEquals(1, errors.size)
        assertEquals(PasswordValidationErrorCode.NEEDS_SPECIAL_CHAR, errors[0].code)
    }
}