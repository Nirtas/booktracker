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
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert
import ru.jerael.booktracker.backend.data.db.tables.EmailVerifications
import ru.jerael.booktracker.backend.data.mappers.toVerificationCode
import ru.jerael.booktracker.backend.domain.model.verification.VerificationCode
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import java.time.LocalDateTime
import java.util.*

class VerificationRepositoryImpl : VerificationRepository {
    override suspend fun saveCode(userId: UUID, code: String, expiresAt: LocalDateTime) {
        withContext(Dispatchers.IO) {
            transaction {
                EmailVerifications.upsert {
                    it[EmailVerifications.userId] = userId
                    it[EmailVerifications.code] = code
                    it[EmailVerifications.expiresAt] = expiresAt
                }
            }
        }
    }

    override suspend fun getCode(userId: UUID): VerificationCode? {
        return withContext(Dispatchers.IO) {
            transaction {
                EmailVerifications
                    .selectAll()
                    .where { EmailVerifications.userId eq userId }
                    .mapNotNull { it.toVerificationCode() }
                    .singleOrNull()
            }
        }
    }

    override suspend fun deleteCode(userId: UUID) {
        withContext(Dispatchers.IO) {
            transaction {
                EmailVerifications.deleteWhere { EmailVerifications.userId eq userId }
            }
        }
    }
}