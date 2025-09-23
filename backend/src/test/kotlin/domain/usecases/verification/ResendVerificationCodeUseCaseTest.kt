package domain.usecases.verification

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ForbiddenException
import ru.jerael.booktracker.backend.domain.exceptions.UserByEmailNotFoundException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.VerificationService
import ru.jerael.booktracker.backend.domain.usecases.verification.ResendVerificationCodeUseCase
import java.util.*

class ResendVerificationCodeUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var verificationService: VerificationService

    private lateinit var useCase: ResendVerificationCodeUseCase

    private val email = "test@example.com"
    private val unverifiedUser = User(
        id = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe"),
        email = email,
        passwordHash = "hash",
        isVerified = false
    )
    private val verifiedUser = unverifiedUser.copy(isVerified = true)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = ResendVerificationCodeUseCase(userRepository, verificationService)
    }

    @Test
    fun `when user exists and is not verified, it should start verification process`() = runTest {
        coEvery { userRepository.getUserByEmail(email) } returns unverifiedUser
        coEvery { verificationService.start(unverifiedUser) } just Runs

        useCase.invoke(email)

        coVerify(exactly = 1) { userRepository.getUserByEmail(email) }
        coVerify(exactly = 1) { verificationService.start(unverifiedUser) }
    }

    @Test
    fun `when user does not exist, a UserByEmailNotFoundException should be thrown`() = runTest {
        val nonExistentEmail = "notfound@example.com"
        coEvery { userRepository.getUserByEmail(nonExistentEmail) } returns null

        assertThrows<UserByEmailNotFoundException> {
            useCase.invoke(nonExistentEmail)
        }

        coVerify(exactly = 0) { verificationService.start(any()) }
    }

    @Test
    fun `when user is already verified, a ForbiddenException should be thrown`() = runTest {
        val verifiedEmail = "verified@example.com"
        coEvery { userRepository.getUserByEmail(verifiedEmail) } returns verifiedUser

        assertThrows<ForbiddenException> {
            useCase.invoke(verifiedEmail)
        }

        coVerify(exactly = 0) { verificationService.start(any()) }
    }
}