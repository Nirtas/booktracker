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
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.user.UserUpdatePasswordPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.usecases.user.UpdateUserPasswordUseCase
import ru.jerael.booktracker.backend.domain.validation.ValidationException
import ru.jerael.booktracker.backend.domain.validation.validator.UserValidator
import java.util.*

class UpdateUserPasswordUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordHasher: PasswordHasher

    @MockK
    private lateinit var userValidator: UserValidator

    private lateinit var useCase: UpdateUserPasswordUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val email = "test@example.com"
    private val password = "Passw0rd!"
    private val newPassword = "Passw0rd@"
    private val hash = "hash"
    private val newHash = "new hash"

    private val user = User(
        id = userId,
        email = email,
        passwordHash = hash,
        isVerified = true
    )
    private val userUpdatePasswordPayload = UserUpdatePasswordPayload(
        userId = userId,
        currentPassword = password,
        newPassword = newPassword
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = UpdateUserPasswordUseCase(userRepository, passwordHasher, userValidator)
    }

    @Test
    fun `when user and current password are valid, it should hash new password and update user`() = runTest {
        every { userValidator.validateUpdatePassword(any()) } just Runs
        coEvery { userRepository.getUserById(userId) } returns user
        every { passwordHasher.verify(password, hash) } returns true
        every { passwordHasher.hash(newPassword) } returns newHash
        coEvery { userRepository.updateUserPassword(userId, newHash) } just Runs

        useCase.invoke(userUpdatePasswordPayload)

        verify(exactly = 1) { passwordHasher.hash(newPassword) }
        coVerify(exactly = 1) { userRepository.updateUserPassword(userId, newHash) }
    }

    @Test
    fun `when user is not found, a UserByIdNotFoundException should be thrown`() = runTest {
        every { userValidator.validateUpdatePassword(any()) } just Runs
        coEvery { userRepository.getUserById(userId) } returns null

        assertThrows<UserByIdNotFoundException> {
            useCase.invoke(userUpdatePasswordPayload)
        }
    }

    @Test
    fun `when current password verification fails, a PasswordVerificationException should be thrown`() = runTest {
        every { userValidator.validateUpdatePassword(any()) } just Runs
        coEvery { userRepository.getUserById(userId) } returns user
        every { passwordHasher.verify(password, hash) } returns false

        assertThrows<PasswordVerificationException> {
            useCase.invoke(userUpdatePasswordPayload)
        }

        verify(exactly = 0) { passwordHasher.hash(any()) }
        coVerify(exactly = 0) { userRepository.updateUserPassword(any(), any()) }
    }

    @Test
    fun `when validation is failed, a ValidationException should be thrown`() = runTest {
        val exception = ValidationException(mapOf())
        every { userValidator.validateUpdatePassword(any()) } throws exception

        assertThrows<ValidationException> {
            useCase.invoke(userUpdatePasswordPayload)
        }

        verify(exactly = 0) { passwordHasher.hash(any()) }
        coVerify(exactly = 0) { userRepository.updateUserPassword(any(), any()) }
    }
}