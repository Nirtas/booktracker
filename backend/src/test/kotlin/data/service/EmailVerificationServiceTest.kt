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

package data.service

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.data.service.EmailVerificationService
import ru.jerael.booktracker.backend.domain.config.SmtpConfig
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import ru.jerael.booktracker.backend.domain.service.OtpGenerator
import java.util.*

class EmailVerificationServiceTest {

    @MockK
    private lateinit var verificationRepository: VerificationRepository

    @MockK
    private lateinit var otpGenerator: OtpGenerator

    private lateinit var service: EmailVerificationService

    private val smtpConfig: SmtpConfig = SmtpConfig(
        host = "host",
        port = 1234,
        user = "user",
        password = "password",
        from = "from",
        ssl = false
    )
    private val otpValidityMinutes: Long = 15L

    private val user = User(
        id = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe"),
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = false
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = EmailVerificationService(verificationRepository, otpGenerator, smtpConfig, otpValidityMinutes)
    }

    @Test
    fun `when start is called, it should save the code from generator`() = runTest {
        val expectedCode = "123456"
        val serviceSpy = spyk<EmailVerificationService>(service, recordPrivateCalls = true)
        val codeSlot = slot<VerificationCode>()
        every { otpGenerator.generate() } returns expectedCode
        coEvery { verificationRepository.saveCode(capture(codeSlot)) } just Runs
        every { serviceSpy["sendEmail"](any<String>(), any<String>()) } returns Unit

        serviceSpy.start(user)

        assertEquals(user.id, codeSlot.captured.userId)
        assertEquals(expectedCode, codeSlot.captured.code)
        coVerify(exactly = 1) { verificationRepository.saveCode(any()) }
        verify(exactly = 1) { serviceSpy["sendEmail"](any<String>(), any<String>()) }
    }

    @Test
    fun `when verificationRepository throws an exception, start should rethrow it as InternalException`() = runTest {
        val exception = RuntimeException("Error")
        every { otpGenerator.generate() } returns "123456"
        coEvery { verificationRepository.saveCode(any()) } throws exception

        assertThrows<InternalException> {
            service.start(user)
        }
    }
}