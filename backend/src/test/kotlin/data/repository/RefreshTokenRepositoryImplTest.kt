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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.db.tables.RefreshTokens
import ru.jerael.booktracker.backend.data.db.tables.Users
import ru.jerael.booktracker.backend.data.repository.RefreshTokenRepositoryImpl
import ru.jerael.booktracker.backend.domain.model.token.RefreshToken
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import java.time.LocalDateTime
import java.util.*

class RefreshTokenRepositoryImplTest : RepositoryTestBase() {

    private val refreshTokenRepository: RefreshTokenRepository = RefreshTokenRepositoryImpl()

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
    private val firstToken = RefreshToken(
        userId = firstUser.id,
        token = "refresh",
        expiresAt = LocalDateTime.now().plusDays(30L)
    )
    private val newRefreshToken = "refresh2"
    private val nonExistingToken = "nonExistingToken"

    @BeforeEach
    fun setUp() {
        transaction {
            RefreshTokens.deleteAll()
            Users.deleteAll()
            Users.batchInsert(users) { user ->
                this[Users.id] = user.id
                this[Users.email] = user.email
                this[Users.passwordHash] = user.passwordHash
                this[Users.isVerified] = user.isVerified
            }
            RefreshTokens.insert {
                it[this.userId] = firstToken.userId
                it[this.token] = firstToken.token
                it[this.expiresAt] = firstToken.expiresAt
            }
        }
    }

    @Test
    fun `when createToken is called, it should insert a new refresh token`() = runTest {
        val expiresAt = LocalDateTime.now().plusDays(30L)

        refreshTokenRepository.createToken(secondUser.id, newRefreshToken, expiresAt)

        val savedToken = refreshTokenRepository.getToken(newRefreshToken)
        assertNotNull(savedToken)
        assertEquals(secondUser.id, savedToken!!.userId)
        assertEquals(newRefreshToken, savedToken.token)
    }

    @Test
    fun `when getToken is called for an existing token, it should return the correct refresh token`() = runTest {
        val foundToken = refreshTokenRepository.getToken(firstToken.token)

        assertNotNull(foundToken)
        assertEquals(firstToken.token, foundToken!!.token)
        assertEquals(firstToken.userId, foundToken.userId)
    }

    @Test
    fun `when getToken is called for a non-existing token, it should return null`() = runTest {
        val foundToken = refreshTokenRepository.getToken(nonExistingToken)

        assertNull(foundToken)
    }

    @Test
    fun `when deleteToken is called for an existing token, it should remove it from the database`() = runTest {
        refreshTokenRepository.deleteToken(firstToken.token)

        val foundToken = refreshTokenRepository.getToken(firstToken.token)
        assertNull(foundToken)
    }

    @Test
    fun `when deleteToken is called for a non-existing token, it should do nothing and not throw an exception`() =
        runTest {
            refreshTokenRepository.deleteToken(nonExistingToken)
        }
}