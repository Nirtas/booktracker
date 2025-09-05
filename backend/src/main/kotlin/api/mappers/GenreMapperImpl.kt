package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.domain.model.genre.Genre

class GenreMapperImpl : GenreMapper {
    override fun toDto(genre: Genre): GenreDto {
        return genre.toGenreDto()
    }

    override fun toDto(genres: List<Genre>): List<GenreDto> {
        return genres.map { it.toGenreDto() }
    }
}