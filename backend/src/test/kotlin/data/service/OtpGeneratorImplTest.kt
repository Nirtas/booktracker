package data.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.service.OtpGeneratorImpl
import ru.jerael.booktracker.backend.domain.service.OtpGenerator

class OtpGeneratorImplTest {

    private val otpCodeLength = 6
    private val otpGenerator: OtpGenerator = OtpGeneratorImpl(otpCodeLength)

    @Test
    fun `generate should return a string of correct length`() {
        val code = otpGenerator.generate()

        assertEquals(otpCodeLength, code.length)
    }

    @Test
    fun `generate should return a string containing only digits`() {
        val code = otpGenerator.generate()

        assertTrue(code.all { it.isDigit() })
    }
}