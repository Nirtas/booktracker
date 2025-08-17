package ru.jerael.booktracker.android.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_GENRES

@Entity(
    tableName = TABLE_GENRES,
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class GenreEntity(
    @PrimaryKey val id: Int,
    val name: String
)
