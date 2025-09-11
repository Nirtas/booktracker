package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.domain.model.genre.Genre

class GenreMapperImpl : GenreMapper {
    override fun mapGenreToDto(genre: Genre): GenreDto {
        return GenreDto(id = genre.id, name = genre.name)
    }

    override fun mapGenresToDtos(genres: List<Genre>): List<GenreDto> {
        return genres.map { mapGenreToDto(it) }
    }
}