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

package ru.jerael.booktracker.backend.api.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import ru.jerael.booktracker.backend.api.controller.BookController
import ru.jerael.booktracker.backend.api.util.getUserId
import ru.jerael.booktracker.backend.api.util.getUuidFromPath

fun Route.books(
    bookController: BookController
) {
    authenticate("auth-jwt") {
        route("/books") {
            get {
                val userId = call.getUserId()
                bookController.getAllBooks(call, userId)
            }
            post {
                val userId = call.getUserId()
                bookController.addBook(call, userId)
            }
            route("/{id}") {
                get {
                    val userId = call.getUserId()
                    val bookId = call.getUuidFromPath("id")
                    bookController.getBookById(call, userId, bookId)
                }
                delete {
                    val userId = call.getUserId()
                    val bookId = call.getUuidFromPath("id")
                    bookController.deleteBook(call, userId, bookId)
                }
                put {
                    val userId = call.getUserId()
                    val bookId = call.getUuidFromPath("id")
                    bookController.updateBookDetails(call, userId, bookId)
                }
                post("/cover") {
                    val userId = call.getUserId()
                    val bookId = call.getUuidFromPath("id")
                    bookController.updateBookCover(call, userId, bookId)
                }
            }
        }
    }
}