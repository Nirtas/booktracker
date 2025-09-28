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

package domain.usecases.token

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ExpiredRefreshTokenException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidRefreshTokenException
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.token.RefreshToken
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.usecases.token.RefreshTokenUseCase
import java.time.LocalDateTime
import java.util.*

class RefreshTokenUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @MockK
    private lateinit var tokenService: TokenService

    private lateinit var useCase: RefreshTokenUseCase

    private val refreshToken = "token"
    private val userId = UUID.randomUUID()
    private val token = RefreshToken(
        userId = userId,
        token = refreshToken,
        expiresAt = LocalDateTime.now().plusDays(30L)
    )
    private val user = User(
        id = userId,
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = true
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = RefreshTokenUseCase(userRepository, refreshTokenRepository, tokenService)
    }

    @Test
    fun `when token is valid, it should return a new token pair`() = runTest {
        val expectedTokenPair = TokenPair(accessToken = "access", refreshToken = "refresh")
        coEvery { refreshTokenRepository.getToken(refreshToken) } returns token
        coEvery { refreshTokenRepository.deleteToken(refreshToken) } just Runs
        coEvery { userRepository.getUserById(userId) } returns user
        coEvery { tokenService.generateTokenPair(user.id) } returns expectedTokenPair

        val resultTokenPair = useCase.invoke(refreshToken)

        assertEquals(expectedTokenPair, resultTokenPair)
        coVerify(exactly = 1) { refreshTokenRepository.getToken(refreshToken) }
        coVerify(exactly = 1) { refreshTokenRepository.deleteToken(refreshToken) }
        coVerify(exactly = 1) { userRepository.getUserById(userId) }
        coVerify(exactly = 1) { tokenService.generateTokenPair(user.id) }
    }

    @Test
    fun `when refresh token is not found, an InvalidRefreshTokenException should be thrown`() = runTest {
        coEvery { refreshTokenRepository.getToken(refreshToken) } returns null

        assertThrows<InvalidRefreshTokenException> {
            useCase.invoke(refreshToken)
        }

        coVerify(exactly = 0) { refreshTokenRepository.deleteToken(any()) }
        coVerify(exactly = 0) { userRepository.getUserById(any()) }
        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }

    @Test
    fun `when refresh token is expired, an ExpiredRefreshTokenException should be thrown`() = runTest {
        val expiredToken = token.copy(expiresAt = LocalDateTime.now().minusSeconds(1))
        coEvery { refreshTokenRepository.getToken(refreshToken) } returns expiredToken

        assertThrows<ExpiredRefreshTokenException> {
            useCase.invoke(refreshToken)
        }

        coVerify(exactly = 0) { refreshTokenRepository.deleteToken(any()) }
        coVerify(exactly = 0) { userRepository.getUserById(any()) }
        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }

    @Test
    fun `when user associated with token is not found, a UserByIdNotFoundException should be thrown`() = runTest {
        coEvery { refreshTokenRepository.getToken(refreshToken) } returns token
        coEvery { refreshTokenRepository.deleteToken(refreshToken) } just Runs
        coEvery { userRepository.getUserById(userId) } returns null

        assertThrows<UserByIdNotFoundException> {
            useCase.invoke(refreshToken)
        }

        coVerify(exactly = 0) { tokenService.generateTokenPair(any()) }
    }

    @Test
    fun `when an unexpected error occurs in repository, it should propagate the exception`() = runTest {
        val exception = Exception("Error")
        coEvery { refreshTokenRepository.getToken(refreshToken) } throws exception

        val actualException = assertThrows<Exception> {
            useCase.invoke(refreshToken)
        }

        assertEquals(exception, actualException)
    }
}