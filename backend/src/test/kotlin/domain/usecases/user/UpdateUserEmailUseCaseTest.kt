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

package domain.usecases.user

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.PasswordVerificationException
import ru.jerael.booktracker.backend.domain.exceptions.UserAlreadyExistsException
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.user.UserUpdateEmailPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.VerificationService
import ru.jerael.booktracker.backend.domain.usecases.user.UpdateUserEmailUseCase
import java.util.*

class UpdateUserEmailUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordHasher: PasswordHasher

    @MockK
    private lateinit var verificationService: VerificationService

    private lateinit var useCase: UpdateUserEmailUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val newEmail = "new@example.com"
    private val password = "Passw0rd!"
    private val hash = "hash"

    private val user = User(
        id = userId,
        email = "test@example.com",
        passwordHash = hash,
        isVerified = true
    )
    private val userUpdateEmailPayload = UserUpdateEmailPayload(
        id = userId,
        newEmail = newEmail,
        password = password
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateUserEmailUseCase(userRepository, passwordHasher, verificationService)
    }

    @Test
    fun `when user and password are valid and new email is not taken, it should update email and start verification`() =
        runTest {
            val updatedUser = user.copy(email = newEmail, isVerified = false)

            coEvery { userRepository.getUserById(userId) } returns user
            every { passwordHasher.verify(password, hash) } returns true
            coEvery { userRepository.getUserByEmail(newEmail) } returns null
            coEvery { userRepository.updateUserEmail(userId, newEmail) } returns updatedUser
            coEvery { verificationService.start(updatedUser) } just Runs

            useCase.invoke(userUpdateEmailPayload)

            coVerify(exactly = 1) { userRepository.updateUserEmail(userId, newEmail) }
            coVerify(exactly = 1) { verificationService.start(updatedUser) }
        }

    @Test
    fun `when user is not found, a UserByIdNotFoundException should be thrown`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns null

        assertThrows<UserByIdNotFoundException> {
            useCase.invoke(userUpdateEmailPayload)
        }
    }

    @Test
    fun `when password verification fails, a PasswordVerificationException should be thrown`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns user
        every { passwordHasher.verify(password, hash) } returns false

        assertThrows<PasswordVerificationException> {
            useCase.invoke(userUpdateEmailPayload)
        }
    }

    @Test
    fun `when new email is already taken, a UserAlreadyExistsException should be thrown`() = runTest {
        val anotherUser = User(
            id = UUID.randomUUID(),
            email = newEmail,
            passwordHash = "another hash",
            isVerified = true
        )

        coEvery { userRepository.getUserById(userId) } returns user
        every { passwordHasher.verify(password, hash) } returns true
        coEvery { userRepository.getUserByEmail(newEmail) } returns anotherUser

        assertThrows<UserAlreadyExistsException> {
            useCase.invoke(userUpdateEmailPayload)
        }
    }
}