package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.domain.model.genre.Genre

interface GenreMapper {
    fun toDto(genre: Genre): GenreDto
    fun toDto(genres: List<Genre>): List<GenreDto>
}