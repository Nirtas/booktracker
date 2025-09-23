package api.validation.validator

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.dto.verification.VerificationDto
import ru.jerael.booktracker.backend.api.dto.verification.VerificationResendCodeDto
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.VerificationValidator
import kotlin.test.assertTrue

class VerificationValidatorTest {

    private val validator: VerificationValidator = VerificationValidator(6)
    private val verificationDto = VerificationDto(
        userId = "661d6c9d-c4e9-4921-93c5-8b3dd4e57bf3",
        code = "123456"
    )
    private val verificationResendCodeDto = VerificationResendCodeDto(
        email = "test@example.com"
    )

    @Test
    fun `when dto is valid, validateVerification shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateVerification(verificationDto)
        }
    }

    @Test
    fun `when userId is invalid, validateVerification should throw ValidationException`() {
        val invalidDto = verificationDto.copy(userId = "")

        val exception = assertThrows<ValidationException> {
            validator.validateVerification(invalidDto)
        }

        assertTrue(exception.errors.containsKey("userId"))
    }

    @Test
    fun `when multiple fields are invalid, validateVerification should throw ValidationException containing all errors`() {
        val invalidDto = verificationDto.copy(userId = "", code = "")

        val exception = assertThrows<ValidationException> {
            validator.validateVerification(invalidDto)
        }

        assertTrue(exception.errors.containsKey("userId"))
        assertTrue(exception.errors.containsKey("code"))
    }

    @Test
    fun `when dto is valid, validateResending shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateResending(verificationResendCodeDto)
        }
    }

    @Test
    fun `when email is invalid, validateResending should throw ValidationException`() {
        val invalidDto = verificationResendCodeDto.copy(email = "")

        val exception = assertThrows<ValidationException> {
            validator.validateResending(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
    }
}