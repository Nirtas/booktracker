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

package ru.jerael.booktracker.backend.data.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import ru.jerael.booktracker.backend.domain.config.JwtConfig
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.*

class JwtService(
    private val jwtConfig: JwtConfig,
    private val refreshTokenRepository: RefreshTokenRepository
) : TokenService {
    private val secureRandom = SecureRandom()

    override suspend fun generateTokenPair(user: User): TokenPair {
        val accessToken = JWT.create()
            .withAudience(jwtConfig.audience)
            .withIssuer(jwtConfig.issuer)
            .withClaim("userId", user.id.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.accessExpiresInMinutes))
            .sign(Algorithm.HMAC256(jwtConfig.secret))

        val refreshToken = generateRefreshToken()
        val refreshTokenExpiresAt = LocalDateTime.now().plusDays(jwtConfig.refreshExpiresInDays)
        refreshTokenRepository.createToken(user.id, refreshToken, refreshTokenExpiresAt)
        return TokenPair(accessToken, refreshToken)
    }

    private fun generateRefreshToken(): String {
        val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..64)
            .map { chars[secureRandom.nextInt(chars.size)] }
            .joinToString("")
    }
}