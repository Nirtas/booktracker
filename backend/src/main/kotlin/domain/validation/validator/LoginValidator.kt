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

package ru.jerael.booktracker.backend.domain.validation.validator

import ru.jerael.booktracker.backend.domain.model.login.LoginPayload
import ru.jerael.booktracker.backend.domain.util.putIfNotEmpty
import ru.jerael.booktracker.backend.domain.validation.ValidationError
import ru.jerael.booktracker.backend.domain.validation.ValidationException

class LoginValidator {
    fun validateLogin(payload: LoginPayload) {
        val errors = mutableMapOf<String, List<ValidationError>>()
        errors.putIfNotEmpty("email", validateEmail(payload.email))
        errors.putIfNotEmpty("password", validatePassword(payload.password))
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
    }
}