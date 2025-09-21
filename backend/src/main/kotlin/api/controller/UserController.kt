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
import ru.jerael.booktracker.backend.api.util.getUuidFromPath
import ru.jerael.booktracker.backend.api.validation.validator.UserValidator
import ru.jerael.booktracker.backend.domain.usecases.user.*

class UserController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val updateUserEmailUseCase: UpdateUserEmailUseCase,
    private val updateUserPasswordUseCase: UpdateUserPasswordUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val userValidator: UserValidator,
    private val userMapper: UserMapper
) {
    suspend fun register(call: ApplicationCall) {
        val userCreationDto = call.receive<UserCreationDto>()
        userValidator.validateCreation(userCreationDto)
        val userCreationPayload = userMapper.mapCreationDtoToCreationPayload(userCreationDto)
        val newUser = registerUserUseCase(userCreationPayload)
        call.respond(HttpStatusCode.Created, userMapper.mapUserToDto(newUser))
    }

    suspend fun getUserById(call: ApplicationCall) {
        val id = call.getUuidFromPath("id")
        val user = getUserByIdUseCase(id)
        call.respond(HttpStatusCode.OK, userMapper.mapUserToDto(user))
    }

    suspend fun updateUserEmail(call: ApplicationCall) {
        val id = call.getUuidFromPath("id")
        val userUpdateEmailDto = call.receive<UserUpdateEmailDto>()
        userValidator.validateUpdateEmail(userUpdateEmailDto)
        val userUpdateEmailPayload = userMapper.mapUpdateEmailDtoToUpdateEmailPayload(id, userUpdateEmailDto)
        updateUserEmailUseCase(userUpdateEmailPayload)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun updateUserPassword(call: ApplicationCall) {
        val id = call.getUuidFromPath("id")
        val userUpdatePasswordDto = call.receive<UserUpdatePasswordDto>()
        userValidator.validateUpdatePassword(userUpdatePasswordDto)
        val userUpdatePasswordPayload =
            userMapper.mapUpdatePasswordDtoToUpdatePasswordPayload(id, userUpdatePasswordDto)
        updateUserPasswordUseCase(userUpdatePasswordPayload)
        call.respond(HttpStatusCode.OK)
    }

    suspend fun deleteUser(call: ApplicationCall) {
        val id = call.getUuidFromPath("id")
        val userDeletionDto = call.receive<UserDeletionDto>()
        userValidator.validateDeletion(userDeletionDto)
        val userDeletionPayload = userMapper.mapDeletionDtoToDeletionPayload(id, userDeletionDto)
        deleteUserUseCase(userDeletionPayload)
        call.respond(HttpStatusCode.NoContent)
    }
}