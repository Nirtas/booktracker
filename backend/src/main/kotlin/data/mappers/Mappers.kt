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

package ru.jerael.booktracker.backend.data.mappers

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import ru.jerael.booktracker.backend.data.db.tables.EmailVerifications
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.db.tables.RefreshTokens
import ru.jerael.booktracker.backend.data.db.tables.Users
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.model.token.RefreshToken
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode

fun ResultRow.toGenre(nameColumn: Column<String>): Genre {
    return Genre(
        id = this[Genres.id],
        name = this[nameColumn]
    )
}

fun ResultRow.toUser(): User {
    return User(
        id = this[Users.id],
        email = this[Users.email],
        passwordHash = this[Users.passwordHash],
        isVerified = this[Users.isVerified]
    )
}

fun ResultRow.toVerificationCode(): VerificationCode {
    return VerificationCode(
        userId = this[EmailVerifications.userId],
        code = this[EmailVerifications.code],
        expiresAt = this[EmailVerifications.expiresAt]
    )
}

fun ResultRow.toRefreshToken(): RefreshToken {
    return RefreshToken(
        userId = this[RefreshTokens.userId],
        token = this[RefreshTokens.token],
        expiresAt = this[RefreshTokens.expiresAt]
    )
}