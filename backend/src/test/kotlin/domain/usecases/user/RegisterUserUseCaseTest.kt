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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.UserAlreadyExistsException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.user.UserCreationPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.VerificationService
import ru.jerael.booktracker.backend.domain.usecases.user.RegisterUserUseCase
import java.util.*

class RegisterUserUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    @MockK
    private lateinit var passwordHasher: PasswordHasher

    @MockK
    private lateinit var verificationService: VerificationService

    private lateinit var useCase: RegisterUserUseCase

    private val email = "test@example.com"
    private val password = "Passw0rd!"
    private val hash = "hash"

    private val user = User(
        id = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe"),
        email = email,
        passwordHash = hash,
        isVerified = true
    )

    private val userCreationPayload = UserCreationPayload(email, password)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = RegisterUserUseCase(userRepository, passwordHasher, verificationService)
    }

    @Test
    fun `when user does not exist, it should hash password, create user, start verification, and return new user`() =
        runTest {
            coEvery { userRepository.getUserByEmail(email) } returns null
            every { passwordHasher.hash(password) } returns hash
            coEvery { userRepository.createUser(email, hash) } returns user
            coEvery { verificationService.start(user) } just Runs

            val result = useCase.invoke(userCreationPayload)

            assertEquals(user, result)
            coVerify(exactly = 1) { userRepository.getUserByEmail(email) }
            verify(exactly = 1) { passwordHasher.hash(password) }
            coVerify(exactly = 1) { userRepository.createUser(email, hash) }
            coVerify(exactly = 1) { verificationService.start(user) }
        }

    @Test
    fun `when user already exists, a UserAlreadyExistsException should be thrown`() = runTest {
        coEvery { userRepository.getUserByEmail(email) } returns user

        assertThrows<UserAlreadyExistsException> {
            useCase.invoke(userCreationPayload)
        }

        verify(exactly = 0) { passwordHasher.hash(any()) }
        coVerify(exactly = 0) { userRepository.createUser(any(), any()) }
        coVerify(exactly = 0) { verificationService.start(any()) }
    }
}