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

abstract class NotFoundException(
    userMessage: String,
    errorCode: String = "RESOURCE_NOT_FOUND"
) : AppException(
    httpStatusCode = HttpStatusCode.NotFound,
    message = userMessage,
    userMessage = userMessage,
    errorCode = errorCode
)

class BookNotFoundException(bookId: String) : NotFoundException(
    userMessage = "Book with ID '$bookId' was not found.",
    errorCode = "BOOK_NOT_FOUND"
)

class GenreNotFoundException(genreId: Int) : NotFoundException(
    userMessage = "Genre with ID '$genreId' was not found.",
    errorCode = "GENRE_NOT_FOUND"
)

class UserByIdNotFoundException(userId: String) : NotFoundException(
    userMessage = "User with ID '$userId' was not found.",
    errorCode = "USER_WITH_ID_NOT_FOUND"
)

class UserByEmailNotFoundException(email: String) : NotFoundException(
    userMessage = "User with email '$email' was not found.",
    errorCode = "USER_WITH_EMAIL_NOT_FOUND"
)
