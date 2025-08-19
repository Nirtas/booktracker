package ru.jerael.booktracker.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOK_GENRES

@Entity(
    tableName = TABLE_BOOK_GENRES,
    primaryKeys = ["book_id", "genre_id"],
    indices = [
        Index(value = ["genre_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genre_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookGenresEntity(
    @ColumnInfo(name = "book_id") val bookId: String,
    @ColumnInfo(name = "genre_id") val genreId: Int
)
