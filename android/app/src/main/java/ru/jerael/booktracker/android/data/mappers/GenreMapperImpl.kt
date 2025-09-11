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