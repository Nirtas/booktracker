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

package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.user.*
import ru.jerael.booktracker.backend.domain.model.user.*
import java.util.*

class UserMapperImpl : UserMapper {
    override fun mapUserToDto(user: User): UserDto {
        return UserDto(
            id = user.id.toString(),
            email = user.email,
            isVerified = user.isVerified
        )
    }

    override fun mapUsersToDtos(users: List<User>): List<UserDto> {
        return users.map { mapUserToDto(it) }
    }

    override fun mapCreationDtoToCreationPayload(dto: UserCreationDto): UserCreationPayload {
        return UserCreationPayload(
            email = dto.email,
            password = dto.password
        )
    }

    override fun mapUpdateEmailDtoToUpdateEmailPayload(userId: UUID, dto: UserUpdateEmailDto): UserUpdateEmailPayload {
        return UserUpdateEmailPayload(
            id = userId,
            newEmail = dto.newEmail,
            password = dto.password
        )
    }

    override fun mapUpdatePasswordDtoToUpdatePasswordPayload(
        userId: UUID,
        dto: UserUpdatePasswordDto
    ): UserUpdatePasswordPayload {
        return UserUpdatePasswordPayload(
            id = userId,
            currentPassword = dto.currentPassword,
            newPassword = dto.newPassword
        )
    }

    override fun mapDeletionDtoToDeletionPayload(userId: UUID, dto: UserDeletionDto): UserDeletionPayload {
        return UserDeletionPayload(
            id = userId,
            password = dto.currentPassword
        )
    }
}