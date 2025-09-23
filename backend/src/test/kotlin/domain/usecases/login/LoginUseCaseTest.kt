package domain.usecases.login

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ForbiddenException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidCredentialsException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.login.LoginPayload
import ru.jerael.booktracker.backend.domain.model.token.Token
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.usecases.login.LoginUseCase
import java.util.*

class LoginUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordHasher: PasswordHasher

    @MockK
    private lateinit var tokenService: TokenService

    private lateinit var useCase: LoginUseCase

    private val email = "test@example.com"
    private val password = "Passw0rd!"
    private val hash = "hash"

    private val user = User(
        id = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe"),
        email = email,
        passwordHash = hash,
        isVerified = true
    )

    private val loginPayload = LoginPayload(email, password)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = LoginUseCase(userRepository, passwordHasher, tokenService)
    }

    @Test
    fun `when credentials are valid and user is verified, it should return a token`() = runTest {
        val expectedToken = Token(token = "token", expiresIn = 3600L)

        coEvery { userRepository.getUserByEmail(email) } returns user
        every { passwordHasher.verify(password, hash) } returns true
        every { tokenService.generateToken(user) } returns expectedToken

        val resultToken = useCase.invoke(loginPayload)

        assertEquals(expectedToken, resultToken)
        coVerify(exactly = 1) { userRepository.getUserByEmail(email) }
        verify(exactly = 1) { passwordHasher.verify(password, hash) }
        verify(exactly = 1) { tokenService.generateToken(user) }
    }

    @Test
    fun `when user is not found by email, an InvalidCredentialsException should be thrown`() = runTest {
        coEvery { userRepository.getUserByEmail(email) } returns null

        assertThrows<InvalidCredentialsException> {
            useCase.invoke(loginPayload)
        }

        verify(exactly = 0) { passwordHasher.verify(any(), any()) }
        verify(exactly = 0) { tokenService.generateToken(any()) }
    }

    @Test
    fun `when password does not match, an InvalidCredentialsException should be thrown`() = runTest {
        coEvery { userRepository.getUserByEmail(email) } returns user
        every { passwordHasher.verify(password, hash) } returns false

        assertThrows<InvalidCredentialsException> {
            useCase.invoke(loginPayload)
        }

        verify(exactly = 0) { tokenService.generateToken(any()) }
    }

    @Test
    fun `when user is not verified, a ForbiddenException should be thrown`() = runTest {
        val unverifiedUser = user.copy(isVerified = false)
        coEvery { userRepository.getUserByEmail(email) } returns unverifiedUser
        every { passwordHasher.verify(password, unverifiedUser.passwordHash) } returns true

        assertThrows<ForbiddenException> {
            useCase.invoke(loginPayload)
        }

        verify(exactly = 0) { tokenService.generateToken(any()) }
    }

    @Test
    fun `when an unexpected error occurs in repository, it should propagate the exception`() = runTest {
        val exception = Exception("Error")
        coEvery { userRepository.getUserByEmail(email) } throws exception

        val actualException = assertThrows<Exception> {
            useCase.invoke(loginPayload)
        }

        assertEquals(exception, actualException)
    }
}