package ru.jerael.booktracker.backend.data.mappers

import ru.jerael.booktracker.backend.data.dto.BookDto
import ru.jerael.booktracker.backend.domain.model.Book

fun Book.toBookDto(imageBaseUrl: String): BookDto {
    return BookDto(
        id = this.id.toString(),
        title = this.title,
        author = this.author,
        coverUrl = this.coverPath?.let { "$imageBaseUrl$it" }
    )
}