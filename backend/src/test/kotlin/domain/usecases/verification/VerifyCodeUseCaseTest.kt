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

package domain.usecases.verification

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.InvalidVerificationException
import ru.jerael.booktracker.backend.domain.exceptions.UserByEmailNotFoundException
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode
import ru.jerael.booktracker.backend.domain.model.verification.VerificationPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.usecases.verification.VerifyCodeUseCase
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class VerifyCodeUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var verificationRepository: VerificationRepository

    @MockK
    private lateinit var tokenService: TokenService

    private lateinit var useCase: VerifyCodeUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val email = "test@example.com"
    private val code = "123456"

    private val user = User(
        id = userId,
        email = email,
        passwordHash = "hash",
        isVerified = false
    )

    private val verificationCode = VerificationCode(
        userId = userId,
        code = code,
        expiresAt = LocalDateTime.now().plusMinutes(15L)
    )

    private val verificationPayload = VerificationPayload(email = email, code = code)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = VerifyCodeUseCase(userRepository, verificationRepository, tokenService)
    }

    @Test
    fun `when code is valid and not expired, it should verify user and delete code`() = runTest {
        val tokenPair = TokenPair("access", "refresh")
        coEvery { userRepository.getUserByEmail(email) } returns user
        coEvery { verificationRepository.getCode(userId) } returns verificationCode
        coEvery { userRepository.updateUserVerificationStatus(userId, true) } just Runs
        coEvery { verificationRepository.deleteCode(userId) } just Runs
        coEvery { tokenService.generateTokenPair(userId) } returns tokenPair

        val result = useCase.invoke(verificationPayload)

        assertEquals(tokenPair, result)
        coVerify(exactly = 1) { userRepository.updateUserVerificationStatus(userId, true) }
        coVerify(exactly = 1) { verificationRepository.deleteCode(userId) }
    }

    @Test
    fun `when user is not found by email, a UserByEmailNotFoundException should be thrown`() = runTest {
        coEvery { userRepository.getUserByEmail(email) } returns null

        assertThrows<UserByEmailNotFoundException> {
            useCase.invoke(verificationPayload)
        }

        coVerify(exactly = 0) { verificationRepository.getCode(any()) }
        coVerify(exactly = 0) { userRepository.updateUserVerificationStatus(any(), any()) }
        coVerify(exactly = 0) { verificationRepository.deleteCode(any()) }
    }

    @Test
    fun `when verification code is not found for the user, an InvalidVerificationException should be thrown`() =
        runTest {
            coEvery { userRepository.getUserByEmail(email) } returns user
            coEvery { verificationRepository.getCode(userId) } returns null

            assertThrows<InvalidVerificationException> {
                useCase.invoke(verificationPayload)
            }
        }

    @Test
    fun `when provided code does not match the stored code, an InvalidVerificationException should be thrown`() =
        runTest {
            val incorrectPayload = verificationPayload.copy(code = "654321")
            coEvery { userRepository.getUserByEmail(email) } returns user
            coEvery { verificationRepository.getCode(userId) } returns verificationCode

            assertThrows<InvalidVerificationException> {
                useCase.invoke(incorrectPayload)
            }
        }

    @Test
    fun `when verification code is expired, an InvalidVerificationException should be thrown`() = runTest {
        val expiredCode = verificationCode.copy(expiresAt = LocalDateTime.now().minusMinutes(1))
        coEvery { userRepository.getUserByEmail(email) } returns user
        coEvery { verificationRepository.getCode(userId) } returns expiredCode

        assertThrows<InvalidVerificationException> {
            useCase.invoke(verificationPayload)
        }
    }
}