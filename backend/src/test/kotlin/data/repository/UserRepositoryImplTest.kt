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

package data.repository

import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import ru.jerael.booktracker.backend.data.db.tables.Users
import ru.jerael.booktracker.backend.data.repository.UserRepositoryImpl
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserRepositoryImplTest : RepositoryTestBase() {

    private val userRepository: UserRepository = UserRepositoryImpl()

    private val firstUser = User(
        id = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe"),
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = true
    )
    private val secondUser = User(
        id = UUID.fromString("6b27add8-d3eb-4cf1-bfe9-8aeb261a00d0"),
        email = "test2@example.com",
        passwordHash = "hash2",
        isVerified = false
    )
    private val users = listOf(firstUser, secondUser)
    private val nonExistentUserId = UUID.fromString("2d49f7f3-e395-49b0-815b-392b0aa62bd0")
    private val nonExistentEmail = "non.existent@mail.com"
    private val newEmail = "new.email@example.com"
    private val newHash = "new-hash"

    @BeforeEach
    fun setUp() {
        transaction {
            Users.deleteAll()
            Users.batchInsert(users) { user ->
                this[Users.id] = user.id
                this[Users.email] = user.email
                this[Users.passwordHash] = user.passwordHash
                this[Users.isVerified] = user.isVerified
            }
        }
    }

    @Test
    fun `when getUserById is called with an existing id, it should return the correct user`() = runTest {
        val result = userRepository.getUserById(firstUser.id)

        assertNotNull(result)
        assertEquals(firstUser.id, result.id)
    }

    @Test
    fun `when getUserById is called with a non-existent id, it should return null`() = runTest {
        val result = userRepository.getUserById(nonExistentUserId)

        assertNull(result)
    }

    @Test
    fun `when getUserByEmail is called with an existing email, it should return the correct user`() = runTest {
        val result = userRepository.getUserByEmail(firstUser.email)

        assertNotNull(result)
        assertEquals(firstUser.id, result.id)
    }

    @Test
    fun `when getUserByEmail is called with an email in a different case, it should return the correct user`() =
        runTest {
            val result = userRepository.getUserByEmail(firstUser.email.uppercase())

            assertNotNull(result)
            assertEquals(firstUser.id, result.id)
        }

    @Test
    fun `when getUserByEmail is called with a non-existent email, it should return null`() = runTest {
        val result = userRepository.getUserByEmail(nonExistentEmail)

        assertNull(result)
    }

    @Test
    fun `when createUser is called with valid data, it should create a user and return the created user object`() =
        runTest {
            val createdBook = userRepository.createUser(newEmail, newHash)

            assertNotNull(createdBook)
            val expectedUser = User(
                id = createdBook.id,
                email = newEmail,
                passwordHash = newHash,
                isVerified = false
            )
            assertEquals(expectedUser, createdBook)
        }

    @Test
    fun `when createUser is called with a duplicate email, an ExposedSQLException for unique constraint violation should be thrown`() =
        runTest {
            assertThrows<ExposedSQLException> {
                userRepository.createUser(firstUser.email, newHash)
            }
        }

    @Test
    fun `when updateUserVerificationStatus is called, it should update the isVerified flag for the correct user`() =
        runTest {
            userRepository.updateUserVerificationStatus(secondUser.id, true)

            val updatedUser = userRepository.getUserById(secondUser.id)
            assertTrue(updatedUser!!.isVerified)
        }

    @Test
    fun `when updateUserEmail is called with valid data, it should update the email and isVerified status and return the updated user`() =
        runTest {
            val updatedUser = userRepository.updateUserEmail(firstUser.id, newEmail)

            assertEquals(newEmail, updatedUser.email)
            assertFalse(updatedUser.isVerified)
        }

    @Test
    fun `when updateUserEmail is called for a non-existent user, it should throw a UserByIdNotFoundException`() =
        runTest {
            assertThrows<UserByIdNotFoundException> {
                userRepository.updateUserEmail(nonExistentUserId, newEmail)
            }
        }

    @Test
    fun `when updateUserPassword is called with valid data, it should update the passwordHash for the correct user`() =
        runTest {
            userRepository.updateUserPassword(firstUser.id, newHash)

            val updatedUser = userRepository.getUserById(firstUser.id)
            assertEquals(newHash, updatedUser?.passwordHash)
        }

    @Test
    fun `when updateUserPassword is called for a non-existent user, it should throw a UserByIdNotFoundException`() =
        runTest {
            assertThrows<UserByIdNotFoundException> {
                userRepository.updateUserPassword(nonExistentUserId, newHash)
            }
        }

    @Test
    fun `when deleteUser is called with an existing id, it should delete the user from the database`() = runTest {
        userRepository.deleteUser(firstUser.id)

        val deletedUser = userRepository.getUserById(firstUser.id)
        assertNull(deletedUser)
    }

    @Test
    fun `when deleteUser is called with a non-existent id, it should throw a UserByIdNotFoundException`() = runTest {
        assertThrows<UserByIdNotFoundException> {
            userRepository.deleteUser(nonExistentUserId)
        }
    }
}