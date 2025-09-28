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

package data.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.service.JwtService
import ru.jerael.booktracker.backend.domain.config.JwtConfig
import ru.jerael.booktracker.backend.domain.model.token.RefreshToken
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import java.time.LocalDateTime
import java.util.*

class JwtServiceTest {

    @MockK
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    private lateinit var service: JwtService

    private val user = User(
        id = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe"),
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = false
    )
    private val jwtConfig = JwtConfig(
        secret = "secret",
        issuer = "issuer",
        audience = "audience",
        realm = "realm",
        accessExpiresInMinutes = 15L,
        refreshExpiresInDays = 30L
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = JwtService(jwtConfig, refreshTokenRepository)
    }

    @Test
    fun `when generateTokenPair is called, it should return a valid token pair`() = runTest {
        coEvery { refreshTokenRepository.createToken(any()) } just Runs

        val tokenPair = service.generateTokenPair(user.id)

        assertNotNull(tokenPair)
        assertNotNull(tokenPair.accessToken)
        assertNotNull(tokenPair.refreshToken)
        assertEquals(64, tokenPair.refreshToken.length)
    }

    @Test
    fun `when generateTokenPair is called, it should create an access token with correct claims`() = runTest {
        val verifier = JWT.require(Algorithm.HMAC256(jwtConfig.secret))
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .acceptLeeway(5L)
            .build()
        coEvery { refreshTokenRepository.createToken(any()) } just Runs

        val tokenPair = service.generateTokenPair(user.id)

        val decodedJWT = verifier.verify(tokenPair.accessToken)
        assertEquals(user.id.toString(), decodedJWT.getClaim("userId").asString())
        assertTrue(decodedJWT.audience.contains(jwtConfig.audience))
        assertEquals(jwtConfig.issuer, decodedJWT.issuer)
    }

    @Test
    fun `when generateTokenPair is called, it should call repository to create a refresh token`() = runTest {
        val refreshTokenSlot = slot<RefreshToken>()
        coEvery { refreshTokenRepository.createToken(capture(refreshTokenSlot)) } returns Unit

        service.generateTokenPair(user.id)

        assertEquals(user.id, refreshTokenSlot.captured.userId)
        assertEquals(64, refreshTokenSlot.captured.token.length)
        val expectedExpiresAt = LocalDateTime.now().plusDays(30)
        val actualExpiresAt = refreshTokenSlot.captured.expiresAt
        assertTrue(actualExpiresAt.isAfter(expectedExpiresAt.minusSeconds(5)))
        assertTrue(actualExpiresAt.isBefore(expectedExpiresAt.plusSeconds(5)))
        coVerify(exactly = 1) { refreshTokenRepository.createToken(any()) }
    }
}