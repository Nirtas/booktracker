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
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.usecases.login.LoginUseCase
import ru.jerael.booktracker.backend.domain.validation.ValidationException
import ru.jerael.booktracker.backend.domain.validation.validator.LoginValidator
import java.util.*

class LoginUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordHasher: PasswordHasher

    @MockK
    private lateinit var tokenService: TokenService

    @MockK
    private lateinit var loginValidator: LoginValidator

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
        useCase = LoginUseCase(userRepository, passwordHasher, tokenService, loginValidator)
    }

    @Test
    fun `when credentials are valid and user is verified, it should return a token`() = runTest {
        val expectedToken = TokenPair(accessToken = "access", refreshToken = "refresh")

        every { loginValidator.validateLogin(loginPayload) } just Runs
        coEvery { userRepository.getUserByEmail(email) } returns user
        every { passwordHasher.verify(password, hash) } returns true
        coEvery { tokenService.generateTokenPair(user.id) } returns expectedToken

        val resultToken = useCase.invoke(loginPayload)

        assertEquals(expectedToken, resultToken)
        coVerify(exactly = 1) { userRepository.getUserByEmail(email) }
        verify(exactly = 1) { passwordHasher.verify(password, hash) }
        coVerify(exactly = 1) { tokenService.generateTokenPair(user.id) }
    }

    @Test
    fun `when user is not found by email, an InvalidCredentialsException should be thrown`() = runTest {
        every { loginValidator.validateLogin(loginPayload) } just Runs
        coEvery { userRepository.getUserByEmail(email) } returns null

        assertThrows<InvalidCredentialsException> {
            useCase.invoke(loginPayload)
        }

        verify(exactly = 0) { passwordHasher.verify(any(), any()) }
        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }

    @Test
    fun `when password does not match, an InvalidCredentialsException should be thrown`() = runTest {
        every { loginValidator.validateLogin(loginPayload) } just Runs
        coEvery { userRepository.getUserByEmail(email) } returns user
        every { passwordHasher.verify(password, hash) } returns false

        assertThrows<InvalidCredentialsException> {
            useCase.invoke(loginPayload)
        }

        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }

    @Test
    fun `when user is not verified, a ForbiddenException should be thrown`() = runTest {
        val unverifiedUser = user.copy(isVerified = false)
        every { loginValidator.validateLogin(loginPayload) } just Runs
        coEvery { userRepository.getUserByEmail(email) } returns unverifiedUser
        every { passwordHasher.verify(password, unverifiedUser.passwordHash) } returns true

        assertThrows<ForbiddenException> {
            useCase.invoke(loginPayload)
        }

        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }

    @Test
    fun `when an unexpected error occurs in repository, it should propagate the exception`() = runTest {
        val exception = Exception("Error")
        every { loginValidator.validateLogin(loginPayload) } just Runs
        coEvery { userRepository.getUserByEmail(email) } throws exception

        val actualException = assertThrows<Exception> {
            useCase.invoke(loginPayload)
        }

        assertEquals(exception, actualException)
    }

    @Test
    fun `when login validation is failed, a ValidationException should be thrown`() = runTest {
        val exception = ValidationException(mapOf())
        every { loginValidator.validateLogin(any()) } throws exception

        assertThrows<ValidationException> {
            useCase.invoke(loginPayload)
        }

        coVerify(exactly = 0) { userRepository.getUserByEmail(any()) }
        verify(exactly = 0) { passwordHasher.verify(any(), any()) }
        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }
}