package ru.jerael.booktracker.android.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.local.entity.GenreEntity

data class BookWithGenres(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookGenresEntity::class,
            parentColumn = "book_id",
            entityColumn = "genre_id"
        )
    )
    val genres: List<GenreEntity>
)
