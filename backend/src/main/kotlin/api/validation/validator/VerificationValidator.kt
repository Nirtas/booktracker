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

import ru.jerael.booktracker.backend.api.config.OtpConfig
import ru.jerael.booktracker.backend.api.dto.verification.VerificationDto
import ru.jerael.booktracker.backend.api.dto.verification.VerificationResendCodeDto
import ru.jerael.booktracker.backend.api.util.putIfNotEmpty
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.CodeValidationErrorCode
import ru.jerael.booktracker.backend.api.validation.codes.CommonValidationErrorCode

class VerificationValidator(private val otpConfig: OtpConfig) {
    fun validateVerification(dto: VerificationDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("userId", validateUserId(dto.userId))
        errors.putIfNotEmpty("code", validateCode(dto.code))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    fun validateResending(dto: VerificationResendCodeDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("email", validateEmail(dto.email))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }

    private fun validateCode(code: String): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        if (code.isBlank()) {
            errors.add(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))
        } else {
            if (code.length != otpConfig.length) {
                errors.add(
                    ValidationError(
                        code = CodeValidationErrorCode.LENGTH_INVALID,
                        params = mapOf("length" to listOf(otpConfig.length.toString()))
                    )
                )
            }
            if (!code.all { it.isDigit() }) {
                errors.add(ValidationError(CodeValidationErrorCode.MUST_BE_DIGITS))
            }
        }
        return errors
    }
}