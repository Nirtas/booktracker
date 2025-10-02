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

package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.dto.user.UserCreationDto
import ru.jerael.booktracker.backend.api.dto.user.UserDeletionDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdateEmailDto
import ru.jerael.booktracker.backend.api.dto.user.UserUpdatePasswordDto
import ru.jerael.booktracker.backend.api.mappers.UserMapper
import ru.jerael.booktracker.backend.domain.model.user.UserCreationPayload
import ru.jerael.booktracker.backend.domain.model.user.UserDeletionPayload
import ru.jerael.booktracker.backend.domain.model.user.UserUpdateEmailPayload
import ru.jerael.booktracker.backend.domain.model.user.UserUpdatePasswordPayload
import ru.jerael.booktracker.backend.domain.usecases.user.*
import java.util.*

class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserEmailUseCase: UpdateUserEmailUseCase,
    private val updateUserPasswordUseCase: UpdateUserPasswordUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val userMapper: UserMapper
) {
    suspend fun register(call: ApplicationCall) {
        val userCreationDto = call.receive<UserCreationDto>()
        val userCreationPayload = UserCreationPayload(
            email = userCreationDto.email,
            password = userCreationDto.password
        )
        val newUser = registerUserUseCase(userCreationPayload)
        call.respond(HttpStatusCode.Created, userMapper.mapUserToDto(newUser))
    }

    suspend fun getUserById(call: ApplicationCall, userId: UUID) {
        val user = getUserByIdUseCase(userId)
        call.respond(HttpStatusCode.OK, userMapper.mapUserToDto(user))
    }

    suspend fun updateUserEmail(call: ApplicationCall, userId: UUID) {
        val userUpdateEmailDto = call.receive<UserUpdateEmailDto>()
        val userUpdateEmailPayload = UserUpdateEmailPayload(
            userId = userId,
            newEmail = userUpdateEmailDto.newEmail,
            password = userUpdateEmailDto.password
        )
        updateUserEmailUseCase(userUpdateEmailPayload)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun updateUserPassword(call: ApplicationCall, userId: UUID) {
        val userUpdatePasswordDto = call.receive<UserUpdatePasswordDto>()
        val userUpdatePasswordPayload = UserUpdatePasswordPayload(
            userId = userId,
            currentPassword = userUpdatePasswordDto.currentPassword,
            newPassword = userUpdatePasswordDto.newPassword
        )
        updateUserPasswordUseCase(userUpdatePasswordPayload)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun deleteUser(call: ApplicationCall, userId: UUID) {
        val userDeletionDto = call.receive<UserDeletionDto>()
        val userDeletionPayload = UserDeletionPayload(
            userId = userId,
            password = userDeletionDto.currentPassword
        )
        deleteUserUseCase(userDeletionPayload)
        call.respond(HttpStatusCode.NoContent)
    }
}