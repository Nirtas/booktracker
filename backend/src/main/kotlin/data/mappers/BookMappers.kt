package ru.jerael.booktracker.backend.data.mappers

import org.jetbrains.exposed.v1.core.ResultRow
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.dto.book.BookDto
import ru.jerael.booktracker.backend.domain.model.book.Book

fun Book.toBookDto(imageBaseUrl: String): BookDto {
    return BookDto(
        id = this.id.toString(),
        title = this.title,
        author = this.author,
        coverUrl = this.coverPath?.let { "$imageBaseUrl/$it" }
    )
}

fun ResultRow.toBook(): Book {
    return Book(
        id = this[Books.id],
        title = this[Books.title],
        author = this[Books.author],
        coverPath = this[Books.coverPath]
    )
}
