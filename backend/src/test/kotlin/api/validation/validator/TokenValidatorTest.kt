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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.dto.token.RefreshTokenDto
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.TokenValidator

class TokenValidatorTest {

    private val validator = TokenValidator()

    private val refreshTokenDto = RefreshTokenDto(
        refreshToken = "1MyFCt1uud8iM5M6TV0Dc46re6nUip3HeKyrATPbXOYxDcITwBsyAu2HN6gYIx7z"
    )

    @Test
    fun `when dto is valid, validateRefresh should not throw exception`() {
        assertDoesNotThrow {
            validator.validateRefresh(refreshTokenDto)
        }
    }

    @Test
    fun `when refreshToken is invalid, validateRefresh should throw ValidationException`() {
        val invalidDto = refreshTokenDto.copy(refreshToken = "")

        val exception = assertThrows<ValidationException> {
            validator.validateRefresh(invalidDto)
        }

        assertEquals(1, exception.errors.size)
        assertTrue(exception.errors.containsKey("refreshToken"))
    }
}