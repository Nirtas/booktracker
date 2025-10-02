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
import ru.jerael.booktracker.backend.domain.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.domain.validation.codes.RefreshTokenValidationErrorCode
import ru.jerael.booktracker.backend.domain.validation.validator.validateRefreshToken
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateRefreshTokenTest {

    private val refreshTokenLength = 64

    @Test
    fun `when token is valid, validateRefreshToken should return an empty list`() {
        val validToken = "1MyFCt1uud8iM5M6TV0Dc46re6nUip3HeKyrATPbXOYxDcITwBsyAu2HN6gYIx7z"

        val errors = validateRefreshToken(validToken)

        assertTrue(errors.isEmpty())
    }

    @Test
    fun `when token is empty, validateRefreshToken should return FIELD_CANNOT_BE_EMPTY error`() {
        val invalidToken = ""

        val errors = validateRefreshToken(invalidToken)

        assertEquals(1, errors.size)
        assertEquals(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY, errors[0].code)
    }

    @Test
    fun `when token is shorter than required, validateRefreshToken should return LENGTH_INVALID error`() {
        val invalidToken = "a".repeat(refreshTokenLength - 1)

        val errors = validateRefreshToken(invalidToken)

        assertEquals(1, errors.size)
        assertEquals(RefreshTokenValidationErrorCode.LENGTH_INVALID, errors[0].code)
    }

    @Test
    fun `when token is longer than required, validateRefreshToken should return LENGTH_INVALID error`() {
        val invalidToken = "a".repeat(refreshTokenLength + 1)

        val errors = validateRefreshToken(invalidToken)

        assertEquals(1, errors.size)
        assertEquals(RefreshTokenValidationErrorCode.LENGTH_INVALID, errors[0].code)
    }

    @Test
    fun `when token contains invalid characters, validateRefreshToken should return INVALID_FORMAT error`() {
        val invalidToken = "a".repeat(refreshTokenLength - 1) + "!"

        val errors = validateRefreshToken(invalidToken)

        assertEquals(1, errors.size)
        assertEquals(RefreshTokenValidationErrorCode.INVALID_FORMAT, errors[0].code)
    }

    @Test
    fun `when token has wrong length and invalid format, validateRefreshToken should return both errors`() {
        val invalidToken = "!".repeat(refreshTokenLength - 1)

        val errors = validateRefreshToken(invalidToken)
        val errorCodes = errors.map { it.code }

        assertEquals(2, errors.size)
        assertTrue(errorCodes.contains(RefreshTokenValidationErrorCode.LENGTH_INVALID))
        assertTrue(errorCodes.contains(RefreshTokenValidationErrorCode.INVALID_FORMAT))
    }
}