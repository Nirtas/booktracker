package ru.jerael.booktracker.backend.data.db.tables

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object BookGenres : Table("book_genres") {
    val bookId = uuid("book_id").references(Books.id, onDelete = ReferenceOption.CASCADE)
    val genreId = integer("genre_id").references(Genres.id, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(columns = arrayOf(bookId, genreId), name = "book_genres_pkey")
}

