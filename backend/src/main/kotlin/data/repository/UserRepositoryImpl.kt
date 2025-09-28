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
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import ru.jerael.booktracker.backend.data.db.tables.Users
import ru.jerael.booktracker.backend.data.mappers.toUser
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import java.util.*

class UserRepositoryImpl : UserRepository {
    override suspend fun getUserById(userId: UUID): User? {
        return withContext(Dispatchers.IO) {
            findUser { Users.id eq userId }
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return withContext(Dispatchers.IO) {
            findUser { Users.email.lowerCase() eq email.lowercase() }
        }
    }

    override suspend fun createUser(email: String, passwordHash: String): User {
        return withContext(Dispatchers.IO) {
            transaction {
                val result = Users.insert {
                    it[Users.email] = email
                    it[Users.passwordHash] = passwordHash
                }
                val newUserId = result[Users.id]
                findUser { Users.id eq newUserId }
                    ?: throw InternalException(message = "User with ID $newUserId was created but could not be found immediately after.")
            }
        }
    }

    override suspend fun updateUserVerificationStatus(userId: UUID, isVerified: Boolean) {
        withContext(Dispatchers.IO) {
            transaction {
                Users.update({ Users.id eq userId }) {
                    it[Users.isVerified] = isVerified
                }
            }
        }
    }

    override suspend fun updateUserEmail(userId: UUID, newEmail: String): User {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Users.update({ Users.id eq userId }) {
                    it[Users.email] = newEmail
                    it[Users.isVerified] = false
                }
                if (updatedRows == 0) {
                    throw UserByIdNotFoundException(userId.toString())
                }
                findUser { Users.id eq userId }
                    ?: throw InternalException(message = "User with ID $userId was updated but could not be found immediately after.")
            }
        }
    }

    override suspend fun updateUserPassword(userId: UUID, newPasswordHash: String) {
        return withContext(Dispatchers.IO) {
            transaction {
                val updatedRows = Users.update({ Users.id eq userId }) {
                    it[Users.passwordHash] = newPasswordHash
                }
                if (updatedRows == 0) {
                    throw UserByIdNotFoundException(userId.toString())
                }
            }
        }
    }

    override suspend fun deleteUser(userId: UUID) {
        withContext(Dispatchers.IO) {
            transaction {
                val deletedRows = Users.deleteWhere { Users.id eq userId }
                if (deletedRows == 0) {
                    throw UserByIdNotFoundException(userId.toString())
                }
            }
        }
    }

    private fun findUser(wherePredicate: (SqlExpressionBuilder.() -> Op<Boolean>)? = null): User? {
        return transaction {
            val query = Users.selectAll()
            wherePredicate?.let { query.where(it) }
            query.map { it.toUser() }.singleOrNull()
        }
    }
}