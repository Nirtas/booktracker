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

import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.domain.validation.codes.CodeValidationErrorCode
import ru.jerael.booktracker.backend.domain.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.domain.validation.validator.validateCode
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateCodeTest {

    private val otpCodeLength = 6

    @Test
    fun `when code is valid, validateCode should return an empty list`() {
        val validCode = "123456"

        val errors = validateCode(validCode, otpCodeLength)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `when code is empty, validateCode should return FIELD_CANNOT_BE_EMPTY error and not other errors`() {
        val invalidCode = ""

        val errors = validateCode(invalidCode, otpCodeLength)

        assertEquals(1, errors.size)
        assertEquals(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY, errors[0].code)
    }

    @Test
    fun `when code is shorter than required, validateCode should return LENGTH_INVALID error`() {
        val invalidCode = "123"

        val errors = validateCode(invalidCode, otpCodeLength)

        assertEquals(1, errors.size)
        assertEquals(CodeValidationErrorCode.LENGTH_INVALID, errors[0].code)
    }

    @Test
    fun `when code is longer than required, validateCode should return LENGTH_INVALID error`() {
        val invalidCode = "1234567"

        val errors = validateCode(invalidCode, otpCodeLength)

        assertEquals(1, errors.size)
        assertEquals(CodeValidationErrorCode.LENGTH_INVALID, errors[0].code)
    }

    @Test
    fun `when code contains non-digit characters, validateCode should return MUST_BE_DIGITS error`() {
        val invalidCode = "123a56"

        val errors = validateCode(invalidCode, otpCodeLength)

        assertEquals(1, errors.size)
        assertEquals(CodeValidationErrorCode.MUST_BE_DIGITS, errors[0].code)
    }

    @Test
    fun `when code has both wrong length and non-digits, validateCode should return both errors`() {
        val invalidCode = "1234xyz"

        val errors = validateCode(invalidCode, otpCodeLength)

        assertEquals(2, errors.size)
        assertEquals(CodeValidationErrorCode.LENGTH_INVALID, errors[0].code)
        assertEquals(CodeValidationErrorCode.MUST_BE_DIGITS, errors[1].code)
    }
}