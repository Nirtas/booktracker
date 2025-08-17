package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.GenreEntity
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto
import ru.jerael.booktracker.android.domain.model.genre.Genre

fun GenreEntity.toGenre(): Genre {
    return Genre(
        id = this.id,
        name = this.name
    )
}

fun GenreDto.toGenreEntity(): GenreEntity {
    return GenreEntity(
        id = this.id,
        name = this.name
    )
}
