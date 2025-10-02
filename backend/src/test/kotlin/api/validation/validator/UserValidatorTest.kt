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
import ru.jerael.booktracker.backend.domain.model.user.UserCreationPayload
import ru.jerael.booktracker.backend.domain.model.user.UserDeletionPayload
import ru.jerael.booktracker.backend.domain.model.user.UserUpdateEmailPayload
import ru.jerael.booktracker.backend.domain.model.user.UserUpdatePasswordPayload
import ru.jerael.booktracker.backend.domain.validation.ValidationException
import ru.jerael.booktracker.backend.domain.validation.validator.UserValidator
import java.util.*
import kotlin.test.assertTrue

class UserValidatorTest {

    private val validator: UserValidator = UserValidator()
    private val userCreationPayload = UserCreationPayload(
        email = "test@example.com",
        password = "Passw0rd!"
    )
    private val userUpdateEmailPayload = UserUpdateEmailPayload(
        userId = UUID.randomUUID(),
        newEmail = "test@example.com",
        password = "Passw0rd!"
    )
    private val userUpdatePasswordPayload = UserUpdatePasswordPayload(
        userId = UUID.randomUUID(),
        currentPassword = "Passw0rd!",
        newPassword = "Passw0rd@"
    )
    private val userDeletionPayload = UserDeletionPayload(
        userId = UUID.randomUUID(),
        password = "Passw0rd!"
    )

    @Test
    fun `when dto is valid, validateCreation shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateCreation(userCreationPayload)
        }
    }

    @Test
    fun `when email is invalid, validateCreation should throw ValidationException`() {
        val invalidPayload = userCreationPayload.copy(email = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("email"))
    }

    @Test
    fun `when multiple fields are invalid, validateCreation should throw ValidationException containing all errors`() {
        val invalidPayload = userCreationPayload.copy(email = "", password = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("email"))
        assertTrue(exception.errors.containsKey("password"))
    }

    @Test
    fun `when dto is valid, validateUpdateEmail shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateUpdateEmail(userUpdateEmailPayload)
        }
    }

    @Test
    fun `when newEmail is invalid, validateUpdateEmail should throw ValidationException`() {
        val invalidPayload = userUpdateEmailPayload.copy(newEmail = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdateEmail(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("newEmail"))
    }

    @Test
    fun `when multiple fields are invalid, validateUpdateEmail should throw ValidationException containing all errors`() {
        val invalidPayload = userUpdateEmailPayload.copy(newEmail = "", password = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdateEmail(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("newEmail"))
        assertTrue(exception.errors.containsKey("password"))
    }

    @Test
    fun `when dto is valid, validateUpdatePassword shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateUpdatePassword(userUpdatePasswordPayload)
        }
    }

    @Test
    fun `when currentPassword is invalid, validateUpdatePassword should throw ValidationException`() {
        val invalidPayload = userUpdatePasswordPayload.copy(currentPassword = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdatePassword(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("currentPassword"))
    }

    @Test
    fun `when multiple fields are invalid, validateUpdatePassword should throw ValidationException containing all errors`() {
        val invalidPayload = userUpdatePasswordPayload.copy(currentPassword = "", newPassword = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdatePassword(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("currentPassword"))
        assertTrue(exception.errors.containsKey("newPassword"))
    }

    @Test
    fun `when dto is valid, validateDeletion shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateDeletion(userDeletionPayload)
        }
    }

    @Test
    fun `when currentPassword is invalid, validateDeletion should throw ValidationException`() {
        val invalidPayload = userDeletionPayload.copy(password = "")

        val exception = assertThrows<ValidationException> {
            validator.validateDeletion(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("password"))
    }
}