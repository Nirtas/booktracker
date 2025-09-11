package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.GenreEntity
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto
import ru.jerael.booktracker.android.domain.model.genre.Genre

interface GenreMapper {
    fun mapEntityToGenre(entity: GenreEntity): Genre
    fun mapEntitiesToGenres(entities: List<GenreEntity>): List<Genre>
    fun mapDtoToEntity(dto: GenreDto): GenreEntity
    fun mapDtosToEntities(dtos: List<GenreDto>): List<GenreEntity>
}