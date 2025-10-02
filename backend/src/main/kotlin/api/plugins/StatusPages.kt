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

package ru.jerael.booktracker.backend.api.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.error_handlers.configureDomainExceptions
import ru.jerael.booktracker.backend.api.error_handlers.configureRequestHandlingExceptions
import ru.jerael.booktracker.backend.api.error_handlers.configureValidationExceptions

fun Application.configureStatusPages() {
    install(StatusPages) {
        configureRequestHandlingExceptions()
        configureValidationExceptions()
        configureDomainExceptions()

        exception<Throwable> { call, cause ->
            this@configureStatusPages.log.error("An unexpected error occurred", cause)
            val errorDto = ErrorDto(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred. Please try again later."
            )
            call.respond(HttpStatusCode.InternalServerError, errorDto)
        }
    }
}
