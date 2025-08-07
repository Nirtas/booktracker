package ru.jerael.booktracker.backend.data.db.tables

import org.jetbrains.exposed.v1.core.Table

object Books : Table("books") {
    val id = uuid("book_id").autoGenerate()
    val title = varchar("title", 100)
    val author = varchar("author", 100)
    val coverPath = varchar("cover_path", 255).nullable()

    override val primaryKey = PrimaryKey(id, name = "book_pkey")
}

