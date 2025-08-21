package ru.jerael.booktracker.backend.data.db.tables

import org.jetbrains.exposed.v1.core.Table
import ru.jerael.booktracker.backend.data.db.DbConstants.TABLE_GENRES

object Genres : Table(TABLE_GENRES) {
    val id = integer("genre_id").autoIncrement()
    val nameEn = text("genre_name_en").uniqueIndex()
    val nameRu = text("genre_name_ru").uniqueIndex()

    override val primaryKey = PrimaryKey(id, name = "genres_pkey")
}

