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

import ru.jerael.booktracker.backend.api.dto.verification.VerificationDto
import ru.jerael.booktracker.backend.api.dto.verification.VerificationResendCodeDto
import ru.jerael.booktracker.backend.api.util.putIfNotEmpty
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException

class VerificationValidator(private val otpCodeLength: Int) {
    fun validateVerification(dto: VerificationDto) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("email", validateEmail(dto.email))
        errors.putIfNotEmpty("code", validateCode(dto.code, otpCodeLength))
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
}