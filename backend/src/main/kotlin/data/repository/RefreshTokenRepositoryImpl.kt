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

package ru.jerael.booktracker.backend.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ru.jerael.booktracker.backend.data.db.tables.RefreshTokens
import ru.jerael.booktracker.backend.data.mappers.toRefreshToken
import ru.jerael.booktracker.backend.domain.model.token.RefreshToken
import ru.jerael.booktracker.backend.domain.repository.RefreshTokenRepository
import java.time.LocalDateTime
import java.util.*

class RefreshTokenRepositoryImpl : RefreshTokenRepository {
    override suspend fun getToken(token: String): RefreshToken? {
        return withContext(Dispatchers.IO) {
            transaction {
                RefreshTokens.selectAll().where { RefreshTokens.token eq token }.map { it.toRefreshToken() }
                    .singleOrNull()
            }
        }
    }

    override suspend fun createToken(userId: UUID, token: String, expiresAt: LocalDateTime) {
        withContext(Dispatchers.IO) {
            transaction {
                RefreshTokens.insert {
                    it[RefreshTokens.userId] = userId
                    it[RefreshTokens.token] = token
                    it[RefreshTokens.expiresAt] = expiresAt
                }
            }
        }
    }

    override suspend fun deleteToken(token: String) {
        withContext(Dispatchers.IO) {
            transaction {
                RefreshTokens.deleteWhere { RefreshTokens.token eq token }
            }
        }
    }
}