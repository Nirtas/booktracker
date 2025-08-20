package ru.jerael.booktracker.backend.data.mappers

import ru.jerael.booktracker.backend.data.dto.book.BookDto
import ru.jerael.booktracker.backend.domain.model.book.Book

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

