package ru.jerael.booktracker.backend.data.mappers

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.dto.genre.GenreDto
import ru.jerael.booktracker.backend.domain.model.genre.Genre

fun Genre.toGenreDto(): GenreDto {
    return GenreDto(
        id = this.id,
        name = this.name
    )
}

fun ResultRow.toGenre(nameColumn: Column<String>): Genre {
    return Genre(
        id = this[Genres.id],
        name = this[nameColumn]
    )
}
