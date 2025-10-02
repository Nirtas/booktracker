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

package ru.jerael.booktracker.backend.domain.usecases.verification

import ru.jerael.booktracker.backend.domain.exceptions.InvalidVerificationException
import ru.jerael.booktracker.backend.domain.exceptions.UserByEmailNotFoundException
import ru.jerael.booktracker.backend.domain.model.token.TokenPair
import ru.jerael.booktracker.backend.domain.model.verification.VerificationPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.validation.validator.VerificationValidator
import java.time.LocalDateTime

class VerifyCodeUseCase(
    private val userRepository: UserRepository,
    private val verificationRepository: VerificationRepository,
    private val tokenService: TokenService,
    private val verificationValidator: VerificationValidator
) {
    suspend operator fun invoke(payload: VerificationPayload): TokenPair {
        verificationValidator.validateVerification(payload)
        val user = userRepository.getUserByEmail(payload.email) ?: throw UserByEmailNotFoundException(
            payload.email
        )
        val foundCode = verificationRepository.getCode(user.id) ?: throw InvalidVerificationException()
        val isCodeValid = foundCode.code == payload.code
        val isCodeExpired = LocalDateTime.now().isAfter(foundCode.expiresAt)
        if (!isCodeValid || isCodeExpired) throw InvalidVerificationException()
        userRepository.updateUserVerificationStatus(user.id, true)
        verificationRepository.deleteCode(user.id)
        return tokenService.generateTokenPair(user.id)
    }
}