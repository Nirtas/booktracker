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

package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.GenreEntity
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto
import ru.jerael.booktracker.android.domain.model.genre.Genre
import javax.inject.Inject

class GenreMapperImpl @Inject constructor() : GenreMapper {
    override fun mapEntityToGenre(entity: GenreEntity): Genre {
        return Genre(id = entity.id, name = entity.name)
    }

    override fun mapEntitiesToGenres(entities: List<GenreEntity>): List<Genre> {
        return entities.map { mapEntityToGenre(it) }
    }

    override fun mapDtoToEntity(dto: GenreDto): GenreEntity {
        return GenreEntity(id = dto.id, name = dto.name)
    }

    override fun mapDtosToEntities(dtos: List<GenreDto>): List<GenreEntity> {
        return dtos.map { mapDtoToEntity(it) }
    }
}