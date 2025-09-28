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

package ru.jerael.booktracker.backend.domain.usecases.login

import ru.jerael.booktracker.backend.domain.exceptions.ForbiddenException
import ru.jerael.booktracker.backend.domain.exceptions.InvalidCredentialsException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.login.LoginPayload
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.service.TokenService

class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService
) {
    suspend operator fun invoke(loginPayload: LoginPayload): TokenPair {
        val user = userRepository.getUserByEmail(loginPayload.email) ?: throw InvalidCredentialsException()
        if (!passwordHasher.verify(loginPayload.password, user.passwordHash)) {
            throw InvalidCredentialsException()
        }
        if (!user.isVerified) {
            throw ForbiddenException(
                userMessage = "Please verify your email before logging in.",
                errorCode = "ACCOUNT_NOT_VERIFIED"
            )
        }
        return tokenService.generateTokenPair(user.id)
    }
}