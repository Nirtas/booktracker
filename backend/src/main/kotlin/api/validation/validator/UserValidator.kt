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

import org.apache.commons.validator.routines.EmailValidator
import ru.jerael.booktracker.backend.api.dto.user.UserCreationDto
import ru.jerael.booktracker.backend.api.dto.user.UserDeletionDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdateEmailDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdatePasswordDto
import ru.jerael.booktracker.backend.api.util.putIfNotEmpty
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.codes.EmailValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.codes.PasswordValidationErrorCode
import ru.jerael.booktracker.backend.domain.util.AuthConstants.MAX_EMAIL_LENGTH
import ru.jerael.booktracker.backend.domain.util.AuthConstants.MAX_PASSWORD_LENGTH
import ru.jerael.booktracker.backend.domain.util.AuthConstants.MIN_PASSWORD_LENGTH
import ru.jerael.booktracker.backend.domain.util.AuthConstants.PASSWORD_ALLOWED_SPECIAL_CHARS

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
        errors.putIfNotEmpty("email", validateEmail(dto.email))
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

    private fun validateEmail(email: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (email.isBlank()) {
            errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
        } else {
            if (email.length > MAX_EMAIL_LENGTH) {
                errors.add(
                    ValidationError(
                        code = CommonValidationErrorCode.FIELD_TOO_LONG,
                        params = mapOf("max" to listOf(MAX_EMAIL_LENGTH.toString()))
                    )
                )
            } else if (!EmailValidator.getInstance().isValid(email)) {
                errors.add(ValidationError(EmailValidationErrorCode.INVALID_FORMAT))
            }
        }
        return errors
    }

    private fun validatePassword(password: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (password.isBlank()) {
            errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
        } else {
            if (password.length < MIN_PASSWORD_LENGTH || password.length > MAX_PASSWORD_LENGTH) {
                errors.add(
                    ValidationError(
                        code = PasswordValidationErrorCode.LENGTH_INVALID,
                        params = mapOf(
                            "min" to listOf(MIN_PASSWORD_LENGTH.toString()),
                            "max" to listOf(MAX_PASSWORD_LENGTH.toString())
                        )
                    )
                )
            }
            if (!password.any { it.isLowerCase() }) errors.add(ValidationError(PasswordValidationErrorCode.NEEDS_LOWERCASE))
            if (!password.any { it.isUpperCase() }) errors.add(ValidationError(PasswordValidationErrorCode.NEEDS_UPPERCASE))
            if (!password.any { it.isDigit() }) errors.add(ValidationError(PasswordValidationErrorCode.NEEDS_DIGIT))
            if (!password.any { it in PASSWORD_ALLOWED_SPECIAL_CHARS }) {
                errors.add(ValidationError(PasswordValidationErrorCode.NEEDS_SPECIAL_CHAR))
            }
        }
        return errors
    }
}