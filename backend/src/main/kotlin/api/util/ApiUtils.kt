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

package ru.jerael.booktracker.backend.api.util

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.CommonValidationErrorCode
import ru.jerael.booktracker.backend.domain.exceptions.UnauthenticatedException
import java.util.*

fun ApplicationRequest.language(): String {
    val languageTag = this.header("Accept-Language")?.substringBefore(",")
    return languageTag?.substringBefore("-")?.lowercase() ?: "en"
}

fun ApplicationCall.getUuidFromPath(parameter: String): UUID {
    val parameterString = this.parameters[parameter]
    if (parameterString.isNullOrBlank()) {
        throw ValidationException(mapOf(parameter to listOf(ValidationError(CommonValidationErrorCode.FIELD_CANNOT_BE_EMPTY))))
    }
    return try {
        UUID.fromString(parameterString)
    } catch (e: Exception) {
        throw ValidationException(mapOf(parameter to listOf(ValidationError(CommonValidationErrorCode.INVALID_UUID_FORMAT))))
    }
}

fun ApplicationCall.getUserId(): UUID {
    val principal =
        this.principal<UserIdPrincipal>() ?: throw UnauthenticatedException("Authentication principal not found.")
    val userIdString = principal.name
    if (userIdString.isBlank()) {
        throw UnauthenticatedException("User id is missing in the token.")
    }
    return try {
        UUID.fromString(userIdString)
    } catch (e: Exception) {
        throw UnauthenticatedException("Invalid user id format in token.")
    }
}

fun MutableMap<String, List<ValidationError>>.putIfNotEmpty(key: String, value: List<ValidationError>) {
    if (value.isNotEmpty()) {
        this[key] = value
    }
}