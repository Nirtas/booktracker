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
import ru.jerael.booktracker.backend.domain.model.user.UserDeletionPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.usecases.user.DeleteUserUseCase
import ru.jerael.booktracker.backend.domain.validation.ValidationException
import ru.jerael.booktracker.backend.domain.validation.validator.UserValidator
import java.util.*

class DeleteUserUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordHasher: PasswordHasher

    @MockK
    private lateinit var userValidator: UserValidator

    private lateinit var useCase: DeleteUserUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val email = "test@example.com"
    private val password = "Passw0rd!"
    private val hash = "hash"

    private val user = User(
        id = userId,
        email = email,
        passwordHash = hash,
        isVerified = true
    )

    private val userDeletionPayload = UserDeletionPayload(userId = userId, password = password)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = DeleteUserUseCase(userRepository, passwordHasher, userValidator)
    }

    @Test
    fun `when user exists and password is correct, it should delete the user`() = runTest {
        every { userValidator.validateDeletion(any()) } just Runs
        coEvery { userRepository.getUserById(userId) } returns user
        every { passwordHasher.verify(password, hash) } returns true
        coEvery { userRepository.deleteUser(userId) } just Runs

        useCase.invoke(userDeletionPayload)

        coVerify(exactly = 1) { userRepository.deleteUser(userId) }
    }

    @Test
    fun `when user does not exist, a UserByIdNotFoundException should be thrown`() = runTest {
        every { userValidator.validateDeletion(any()) } just Runs
        coEvery { userRepository.getUserById(userId) } returns null

        assertThrows<UserByIdNotFoundException> {
            useCase.invoke(userDeletionPayload)
        }

        verify(exactly = 0) { passwordHasher.verify(any(), any()) }
        coVerify(exactly = 0) { userRepository.deleteUser(any()) }
    }

    @Test
    fun `when password is incorrect, a PasswordVerificationException should be thrown`() = runTest {
        every { userValidator.validateDeletion(any()) } just Runs
        coEvery { userRepository.getUserById(userId) } returns user
        every { passwordHasher.verify(password, hash) } returns false

        assertThrows<PasswordVerificationException> {
            useCase.invoke(userDeletionPayload)
        }

        coVerify(exactly = 0) { userRepository.deleteUser(any()) }
    }

    @Test
    fun `when validation is failed, a ValidationException should be thrown`() = runTest {
        val exception = ValidationException(mapOf())
        every { userValidator.validateDeletion(any()) } throws exception

        assertThrows<ValidationException> {
            useCase.invoke(userDeletionPayload)
        }

        verify(exactly = 0) { passwordHasher.verify(any(), any()) }
        coVerify(exactly = 0) { userRepository.deleteUser(any()) }
    }
}