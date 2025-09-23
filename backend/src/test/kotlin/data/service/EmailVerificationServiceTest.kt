package data.service

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.config.SmtpConfig
import ru.jerael.booktracker.backend.data.service.EmailVerificationService
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.model.user.User
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
        val userIdSlot = slot<UUID>()
        val codeSlot = slot<String>()
        val serviceSpy = spyk<EmailVerificationService>(service, recordPrivateCalls = true)
        every { otpGenerator.generate() } returns expectedCode
        coEvery {
            verificationRepository.saveCode(
                capture(userIdSlot),
                capture(codeSlot),
                any()
            )
        } just Runs
        every { serviceSpy["sendEmail"](any<String>(), any<String>()) } returns Unit

        serviceSpy.start(user)

        assertEquals(user.id, userIdSlot.captured)
        assertEquals(expectedCode, codeSlot.captured)
        coVerify(exactly = 1) { verificationRepository.saveCode(any(), any(), any()) }
        verify(exactly = 1) { serviceSpy["sendEmail"](any<String>(), any<String>()) }
    }

    @Test
    fun `when verificationRepository throws an exception, start should rethrow it as InternalException`() = runTest {
        val exception = RuntimeException("Error")
        every { otpGenerator.generate() } returns "123456"
        coEvery { verificationRepository.saveCode(any(), any(), any()) } throws exception

        assertThrows<InternalException> {
            service.start(user)
        }
    }
}