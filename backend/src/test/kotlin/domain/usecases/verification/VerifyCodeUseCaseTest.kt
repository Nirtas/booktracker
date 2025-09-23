package domain.usecases.verification

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.InvalidVerificationException
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode
import ru.jerael.booktracker.backend.domain.model.verification.VerificationPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import ru.jerael.booktracker.backend.domain.usecases.verification.VerifyCodeUseCase
import java.time.LocalDateTime
import java.util.*

class VerifyCodeUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var verificationRepository: VerificationRepository

    private lateinit var useCase: VerifyCodeUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val code = "123456"

    private val user = User(
        id = userId,
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = false
    )

    private val verificationCode = VerificationCode(
        userId = userId,
        code = code,
        expiresAt = LocalDateTime.now().plusMinutes(15L)
    )

    private val verificationPayload = VerificationPayload(userId = userId, code = code)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = VerifyCodeUseCase(userRepository, verificationRepository)
    }

    @Test
    fun `when code is valid and not expired, it should verify user and delete code`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns user
        coEvery { verificationRepository.getCode(userId) } returns verificationCode
        coEvery { userRepository.updateUserVerificationStatus(userId, true) } just Runs
        coEvery { verificationRepository.deleteCode(userId) } just Runs

        useCase.invoke(verificationPayload)

        coVerify(exactly = 1) { userRepository.updateUserVerificationStatus(userId, true) }
        coVerify(exactly = 1) { verificationRepository.deleteCode(userId) }
    }

    @Test
    fun `when user is not found by id, a UserByIdNotFoundException should be thrown`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns null

        assertThrows<UserByIdNotFoundException> {
            useCase.invoke(verificationPayload)
        }

        coVerify(exactly = 0) { verificationRepository.getCode(any()) }
        coVerify(exactly = 0) { userRepository.updateUserVerificationStatus(any(), any()) }
        coVerify(exactly = 0) { verificationRepository.deleteCode(any()) }
    }

    @Test
    fun `when verification code is not found for the user, an InvalidVerificationException should be thrown`() =
        runTest {
            coEvery { userRepository.getUserById(userId) } returns user
            coEvery { verificationRepository.getCode(userId) } returns null

            assertThrows<InvalidVerificationException> {
                useCase.invoke(verificationPayload)
            }
        }

    @Test
    fun `when provided code does not match the stored code, an InvalidVerificationException should be thrown`() =
        runTest {
            val incorrectPayload = verificationPayload.copy(code = "654321")
            coEvery { userRepository.getUserById(userId) } returns user
            coEvery { verificationRepository.getCode(userId) } returns verificationCode

            assertThrows<InvalidVerificationException> {
                useCase.invoke(incorrectPayload)
            }
        }

    @Test
    fun `when verification code is expired, an InvalidVerificationException should be thrown`() = runTest {
        val expiredCode = verificationCode.copy(expiresAt = LocalDateTime.now().minusMinutes(1))
        coEvery { userRepository.getUserById(userId) } returns user
        coEvery { verificationRepository.getCode(userId) } returns expiredCode

        assertThrows<InvalidVerificationException> {
            useCase.invoke(verificationPayload)
        }
    }
}