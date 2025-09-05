package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.genre.Genre

fun Book.toBookDto(imageBaseUrl: String): BookDto {
    return BookDto(
        id = this.id.toString(),
        title = this.title,
        author = this.author,
        coverUrl = this.coverPath?.let { "$imageBaseUrl/$it" },
        status = this.status.value,
        createdAt = this.createdAt.toEpochMilli(),
        genres = this.genres.map { it.toGenreDto() }
    )
}

fun Genre.toGenreDto(): GenreDto {
    return GenreDto(
        id = this.id,
        name = this.name
    )
}