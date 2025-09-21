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

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.jerael.booktracker.backend.api.controller.*
import ru.jerael.booktracker.backend.api.routes.*

fun Application.configureRouting() {
    val bookController: BookController by inject()
    val genreController: GenreController by inject()
    val userController: UserController by inject()
    val tokenController: TokenController by inject()
    val verificationController: VerificationController by inject()

    routing {
        route("/api") {
            books(bookController)
            genres(genreController)
            users(userController)
            tokens(tokenController)
            verifications(verificationController)
        }
    }
}