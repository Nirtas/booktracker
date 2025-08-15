package ru.jerael.booktracker.backend.data.db.tables

import org.jetbrains.exposed.v1.core.Table

object Genres : Table("genres") {
    val id = integer("genre_id").autoIncrement()
    val name = text("genre_name").uniqueIndex()

    override val primaryKey = PrimaryKey(id, name = "genres_pkey")
}

