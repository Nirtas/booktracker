package ru.jerael.booktracker.android.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOKS

@Entity(tableName = TABLE_BOOKS)
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    @ColumnInfo(name = "cover_url") val coverUrl: String?,
    val status: String,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
