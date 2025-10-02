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
import ru.jerael.booktracker.backend.domain.model.verification.VerificationPayload
import ru.jerael.booktracker.backend.domain.validation.ValidationException
import ru.jerael.booktracker.backend.domain.validation.validator.VerificationValidator
import kotlin.test.assertTrue

class VerificationValidatorTest {

    private val validator: VerificationValidator = VerificationValidator(6)
    private val email = "test@example.com"
    private val verificationPayload = VerificationPayload(email, "123456")

    @Test
    fun `when dto is valid, validateVerification shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateVerification(verificationPayload)
        }
    }

    @Test
    fun `when email is invalid, validateVerification should throw ValidationException`() {
        val invalidPayload = verificationPayload.copy(email = "")

        val exception = assertThrows<ValidationException> {
            validator.validateVerification(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("email"))
    }

    @Test
    fun `when multiple fields are invalid, validateVerification should throw ValidationException containing all errors`() {
        val invalidPayload = verificationPayload.copy(email = "", code = "")

        val exception = assertThrows<ValidationException> {
            validator.validateVerification(invalidPayload)
        }

        assertTrue(exception.errors.containsKey("email"))
        assertTrue(exception.errors.containsKey("code"))
    }

    @Test
    fun `when dto is valid, validateResending shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateResending(email)
        }
    }

    @Test
    fun `when email is invalid, validateResending should throw ValidationException`() {
        val invalidEmail = ""

        val exception = assertThrows<ValidationException> {
            validator.validateResending(invalidEmail)
        }

        assertTrue(exception.errors.containsKey("email"))
    }
}