package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import java.time.Instant

fun BookEntity.toBook(): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        coverUrl = this.coverUrl,
        status = BookStatus.fromString(this.status) ?: BookStatus.WANT_TO_READ,
        createdAt = Instant.ofEpochMilli(this.createdAt),
        genres = emptyList()
    )
}

fun BookDto.toBookEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        coverUrl = this.coverUrl,
        status = this.status,
        createdAt = this.createdAt
    )
}

fun BookWithGenres.toBook(): Book {
    return this.book.toBook().copy(
        genres = this.genres.map { it.toGenre() }
    )
}