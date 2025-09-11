package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.domain.model.genre.Genre

interface GenreMapper {
    fun mapGenreToDto(genre: Genre): GenreDto
    fun mapGenresToDtos(genres: List<Genre>): List<GenreDto>
}