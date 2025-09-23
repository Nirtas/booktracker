package api.validation.validator

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.dto.login.LoginRequestDto
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.LoginValidator
import kotlin.test.assertTrue

class LoginValidatorTest {

    private val validator: LoginValidator = LoginValidator()
    private val loginRequestDto = LoginRequestDto(
        email = "test@example.com",
        password = "Passw0rd!"
    )

    @Test
    fun `when dto is valid, validateLogin shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateLogin(loginRequestDto)
        }
    }

    @Test
    fun `when email is invalid, validateLogin should throw ValidationException`() {
        val invalidDto = loginRequestDto.copy(email = "")

        val exception = assertThrows<ValidationException> {
            validator.validateLogin(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
    }

    @Test
    fun `when multiple fields are invalid, validateLogin should throw ValidationException containing all errors`() {
        val invalidDto = loginRequestDto.copy(email = "", password = "")

        val exception = assertThrows<ValidationException> {
            validator.validateLogin(invalidDto)
        }

        assertTrue(exception.errors.containsKey("email"))
        assertTrue(exception.errors.containsKey("password"))
    }
}