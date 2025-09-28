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

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.usecases.user.GetUserByIdUseCase
import java.util.*

class GetUserByIdUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var useCase: GetUserByIdUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val user = User(
        id = userId,
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = true
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetUserByIdUseCase(userRepository)
    }

    @Test
    fun `when getUserById is called with an existing id, it should return the correct user`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns user

        val result = useCase.invoke(userId)

        assertEquals(user, result)
    }

    @Test
    fun `when getUserById is called with an non-existing id, a UserByIdNotFoundException should be thrown`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns null

        val exception = assertThrows<UserByIdNotFoundException> {
            useCase.invoke(userId)
        }

        assertTrue(exception.message!!.contains("$userId"))
    }
}