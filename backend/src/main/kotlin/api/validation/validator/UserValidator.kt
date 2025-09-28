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

package ru.jerael.booktracker.backend.api.validation.validator

import ru.jerael.booktracker.backend.api.dto.user.UserCreationDto
import ru.jerael.booktracker.backend.api.dto.user.UserDeletionDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdateEmailDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdatePasswordDto
import ru.jerael.booktracker.backend.api.util.putIfNotEmpty
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.PasswordValidationErrorCode

class UserValidator {
    fun validateCreation(dto: UserCreationDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("email", validateEmail(dto.email))
        errors.putIfNotEmpty("password", validatePassword(dto.password))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    fun validateUpdateEmail(dto: UserUpdateEmailDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("newEmail", validateEmail(dto.newEmail))
        errors.putIfNotEmpty("password", validatePassword(dto.password))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    fun validateUpdatePassword(dto: UserUpdatePasswordDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("currentPassword", validatePassword(dto.currentPassword))
        errors.putIfNotEmpty("newPassword", validatePassword(dto.newPassword))
        if (dto.currentPassword == dto.newPassword) {
            errors.putIfNotEmpty(
                "newPassword",
                listOf(ValidationError(PasswordValidationErrorCode.CANNOT_BE_SAME_AS_OLD))
            )
        }
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    fun validateDeletion(dto: UserDeletionDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("password", validatePassword(dto.currentPassword))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }
}