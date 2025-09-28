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

package ru.jerael.booktracker.backend.domain.usecases.token

import ru.jerael.booktracker.backend.domain.exceptions.ExpiredRefreshTokenException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidRefreshTokenException
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import java.time.LocalDateTime

class RefreshTokenUseCase(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenService: TokenService
) {
    suspend operator fun invoke(refreshToken: String): TokenPair {
        val token = refreshTokenRepository.getToken(refreshToken) ?: throw InvalidRefreshTokenException()
        if (LocalDateTime.now().isAfter(token.expiresAt)) throw ExpiredRefreshTokenException()
        refreshTokenRepository.deleteToken(refreshToken)
        val user = userRepository.getUserById(token.userId) ?: throw UserByIdNotFoundException(token.userId.toString())
        return tokenService.generateTokenPair(user.id)
    }
}