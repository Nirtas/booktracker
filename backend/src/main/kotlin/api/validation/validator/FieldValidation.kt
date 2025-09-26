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
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.codes.*
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import java.util.*

private const val MAX_TITLE_LENGTH = 500
private const val MAX_AUTHOR_LENGTH = 500
private const val MAX_EMAIL_LENGTH = 200
private const val MIN_PASSWORD_LENGTH = 8
private const val MAX_PASSWORD_LENGTH = 64
private const val PASSWORD_ALLOWED_SPECIAL_CHARS = "!@#\$%^&*()-+"
private const val REFRESH_TOKEN_LENGTH = 64

fun validateTitle(title: String): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()
    if (title.isBlank()) {
        errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
    } else {
        if (title.length > MAX_TITLE_LENGTH) {
            errors.add(
                ValidationError(
                    code = CommonValidationErrorCode.FIELD_TOO_LONG,
                    params = mapOf("max" to listOf(MAX_TITLE_LENGTH.toString()))
                )
            )
        }
    }
    return errors
}

fun validateAuthor(author: String): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()
    if (author.isBlank()) {
        errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
    } else {
        if (author.length > MAX_AUTHOR_LENGTH) {
            errors.add(
                ValidationError(
                    code = CommonValidationErrorCode.FIELD_TOO_LONG,
                    params = mapOf("max" to listOf(MAX_AUTHOR_LENGTH.toString()))
                )
            )
        }
    }
    return errors
}

fun validateStatus(status: String): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()
    if (BookStatus.fromString(status) == null) {
        val allowedStatuses = BookStatus.entries.map { it.value }
        val error = ValidationError(
            code = BookValidationErrorCode.INVALID_STATUS,
            params = mapOf("allowed" to allowedStatuses)
        )
        errors.add(error)
    }
    return errors
}

fun validateEmail(email: String): List<ValidationError> {
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
        }
        if (!EmailValidator.getInstance().isValid(email)) {
            errors.add(ValidationError(EmailValidationErrorCode.INVALID_FORMAT))
        }
    }
    return errors
}

fun validatePassword(password: String): List<ValidationError> {
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

fun validateUserId(userId: String): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()
    if (userId.isBlank()) {
        errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
    } else {
        try {
            UUID.fromString(userId)
        } catch (e: Exception) {
            errors.add(ValidationError(CommonValidationErrorCode.INVALID_UUID_FORMAT))
        }
    }
    return errors
}

fun validateCode(code: String, length: Int): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()
    if (code.isBlank()) {
        errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
    } else {
        if (code.length != length) {
            errors.add(
                ValidationError(
                    code = CodeValidationErrorCode.LENGTH_INVALID,
                    params = mapOf("length" to listOf(length.toString()))
                )
            )
        }
        if (!code.all { it.isDigit() }) {
            errors.add(ValidationError(CodeValidationErrorCode.MUST_BE_DIGITS))
        }
    }
    return errors
}

fun validateRefreshToken(token: String): List<ValidationError> {
    val errors = mutableListOf<ValidationError>()
    if (token.isBlank()) {
        errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
    } else {
        if (token.length != REFRESH_TOKEN_LENGTH) {
            errors.add(
                ValidationError(
                    code = RefreshTokenValidationErrorCode.LENGTH_INVALID,
                    params = mapOf("length" to listOf(REFRESH_TOKEN_LENGTH.toString()))
                )
            )
        }
        val refreshTokenRegex = Regex("^[a-zA-Z0-9]+$")
        if (!token.matches(refreshTokenRegex)) {
            errors.add(ValidationError(RefreshTokenValidationErrorCode.INVALID_FORMAT))
        }
    }
    return errors
}