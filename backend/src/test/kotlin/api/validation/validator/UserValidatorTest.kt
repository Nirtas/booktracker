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
import ru.jerael.booktracker.backend.api.dto.user.UserCreationDto
import ru.jerael.booktracker.backend.api.dto.user.UserDeletionDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdateEmailDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdatePasswordDto
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.UserValidator
import kotlin.test.assertTrue

class UserValidatorTest {

    private val validator: UserValidator = UserValidator()
    private val userCreationDto = UserCreationDto(
        email = "test@example.com",
        password = "Passw0rd!"
    )
    private val userUpdateEmailDto = UserUpdateEmailDto(
        newEmail = "test@example.com",
        password = "Passw0rd!"
    )
    private val userUpdatePasswordDto = UserUpdatePasswordDto(
        email = "test@example.com",
        currentPassword = "Passw0rd!",
        newPassword = "Passw0rd@"
    )
    private val userDeletionDto = UserDeletionDto(
        currentPassword = "Passw0rd!"
    )

    @Test
    fun `when dto is valid, validateCreation shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateCreation(userCreationDto)
        }
    }

    @Test
    fun `when email is invalid, validateCreation should throw ValidationException`() {
        val invalidDto = userCreationDto.copy(email = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
    }

    @Test
    fun `when multiple fields are invalid, validateCreation should throw ValidationException containing all errors`() {
        val invalidDto = userCreationDto.copy(email = "", password = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
        assertTrue(exception.errors.containsKey("password"))
    }

    @Test
    fun `when dto is valid, validateUpdateEmail shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateUpdateEmail(userUpdateEmailDto)
        }
    }

    @Test
    fun `when newEmail is invalid, validateUpdateEmail should throw ValidationException`() {
        val invalidDto = userUpdateEmailDto.copy(newEmail = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdateEmail(invalidDto)
        }

        assertTrue(exception.errors.containsKey("newEmail"))
    }

    @Test
    fun `when multiple fields are invalid, validateUpdateEmail should throw ValidationException containing all errors`() {
        val invalidDto = userUpdateEmailDto.copy(newEmail = "", password = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdateEmail(invalidDto)
        }

        assertTrue(exception.errors.containsKey("newEmail"))
        assertTrue(exception.errors.containsKey("password"))
    }

    @Test
    fun `when dto is valid, validateUpdatePassword shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateUpdatePassword(userUpdatePasswordDto)
        }
    }

    @Test
    fun `when email is invalid, validateUpdatePassword should throw ValidationException`() {
        val invalidDto = userUpdatePasswordDto.copy(email = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdatePassword(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
    }

    @Test
    fun `when multiple fields are invalid, validateUpdatePassword should throw ValidationException containing all errors`() {
        val invalidDto = userUpdatePasswordDto.copy(email = "", currentPassword = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdatePassword(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
        assertTrue(exception.errors.containsKey("currentPassword"))
    }

    @Test
    fun `when dto is valid, validateDeletion shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateDeletion(userDeletionDto)
        }
    }

    @Test
    fun `when currentPassword is invalid, validateDeletion should throw ValidationException`() {
        val invalidDto = userDeletionDto.copy(currentPassword = "")

        val exception = assertThrows<ValidationException> {
            validator.validateDeletion(invalidDto)
        }

        assertTrue(exception.errors.containsKey("password"))
    }
}