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

package ru.jerael.booktracker.backend.domain.usecases.user

import ru.jerael.booktracker.backend.domain.exceptions.PasswordVerificationException
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.model.user.UserUpdatePasswordPayload
import ru.jerael.booktracker.backend.domain.repository.UserRepository

class UpdateUserPasswordUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher
) {
    suspend operator fun invoke(userUpdatePasswordPayload: UserUpdatePasswordPayload) {
        val user = userRepository.getUserById(userUpdatePasswordPayload.userId) ?: throw UserByIdNotFoundException(
            userUpdatePasswordPayload.userId.toString()
        )
        if (!passwordHasher.verify(userUpdatePasswordPayload.currentPassword, user.passwordHash)) {
            throw PasswordVerificationException()
        }
        val newPasswordHash = passwordHasher.hash(userUpdatePasswordPayload.newPassword)
        userRepository.updateUserPassword(
            userId = userUpdatePasswordPayload.userId,
            newPasswordHash = newPasswordHash
        )
    }
}