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
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.*
import ru.jerael.booktracker.backend.data.db.tables.EmailVerifications
import ru.jerael.booktracker.backend.data.db.tables.Users
import ru.jerael.booktracker.backend.data.repository.VerificationRepositoryImpl
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class VerificationRepositoryImplTest : RepositoryTestBase() {

    private val verificationRepository: VerificationRepository = VerificationRepositoryImpl()

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
    private val firstCode = VerificationCode(
        userId = firstUser.id,
        code = "123456",
        expiresAt = LocalDateTime.now().plusMinutes(15L)
    )
    private val newCode = "654321"
    private val newVerificationCode = VerificationCode(
        userId = secondUser.id,
        code = newCode,
        expiresAt = LocalDateTime.now().plusMinutes(15L)
    )

    @BeforeEach
    fun setUp() {
        transaction {
            EmailVerifications.deleteAll()
            Users.deleteAll()
            Users.batchInsert(users) { user ->
                this[Users.id] = user.id
                this[Users.email] = user.email
                this[Users.passwordHash] = user.passwordHash
                this[Users.isVerified] = user.isVerified
            }
            EmailVerifications.insert {
                it[this.userId] = firstCode.userId
                it[this.code] = firstCode.code
                it[this.expiresAt] = firstCode.expiresAt
            }
        }
    }

    @Test
    fun `when saveCode is called for a new user, it should insert a new verification code`() = runTest {
        verificationRepository.saveCode(newVerificationCode)

        val savedCode = verificationRepository.getCode(newVerificationCode.userId)
        assertNotNull(savedCode)
        assertEquals(newVerificationCode.userId, savedCode.userId)
        assertEquals(newVerificationCode.code, savedCode.code)
    }

    @Test
    fun `when saveCode is called for an existing user, it should update the existing verification code`() = runTest {
        verificationRepository.saveCode(newVerificationCode.copy(userId = firstCode.userId))

        val savedCode = verificationRepository.getCode(firstCode.userId)
        assertNotNull(savedCode)
        assertEquals(newCode, savedCode.code)
    }

    @Test
    fun `when getCode is called for a user with an existing code, it should return the correct verification code`() =
        runTest {
            val foundCode = verificationRepository.getCode(firstCode.userId)

            assertNotNull(foundCode)
            assertEquals(firstCode.code, foundCode.code)
        }

    @Test
    fun `when getCode is called for an existing user without code, it should return null`() = runTest {
        val foundCode = verificationRepository.getCode(secondUser.id)

        assertNull(foundCode)
    }

    @Test
    fun `when deleteCode is called for a user with an existing code, it should remove the code from the database`() =
        runTest {
            verificationRepository.deleteCode(firstCode.userId)

            val foundCode = verificationRepository.getCode(firstCode.userId)
            assertNull(foundCode)
        }

    @Test
    fun `when deleteCode is called for a user without a code, it should do nothing and not throw an exception`() =
        runTest {
            assertDoesNotThrow {
                verificationRepository.deleteCode(secondUser.id)
            }
        }
}