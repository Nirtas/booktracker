package ru.jerael.booktracker.backend.data.db.tables

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone
import ru.jerael.booktracker.backend.data.db.DbConstants.TABLE_BOOKS
import ru.jerael.booktracker.backend.data.db.types.bookStatusEnum

object Books : Table(TABLE_BOOKS) {
    val id = uuid("book_id").autoGenerate()
    val title = text("title")
    val author = text("author")
    val coverPath = text("cover_path").nullable()
    val status = bookStatusEnum("status")
    val createdAt = timestampWithTimeZone("created_at").defaultExpression(CurrentTimestampWithTimeZone)

    override val primaryKey = PrimaryKey(id, name = "books_pkey")
}

