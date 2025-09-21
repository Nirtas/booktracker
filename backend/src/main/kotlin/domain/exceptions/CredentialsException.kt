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

package ru.jerael.booktracker.backend.domain.exceptions

import io.ktor.http.*

abstract class CredentialsException(
    httpStatusCode: HttpStatusCode,
    userMessage: String,
    errorCode: String
) : AppException(
    httpStatusCode = httpStatusCode,
    message = userMessage,
    userMessage = userMessage,
    errorCode = errorCode
)

class PasswordVerificationException : CredentialsException(
    httpStatusCode = HttpStatusCode.BadRequest,
    userMessage = "Invalid password provided.",
    errorCode = "PASSWORD_VERIFICATION_FAILED"
)

class InvalidCredentialsException : CredentialsException(
    httpStatusCode = HttpStatusCode.Unauthorized,
    userMessage = "Invalid email or password provided.",
    errorCode = "INVALID_CREDENTIALS"
)