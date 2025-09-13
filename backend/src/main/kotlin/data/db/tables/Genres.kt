/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.backend.data.db.tables

import org.jetbrains.exposed.v1.core.Table
import ru.jerael.booktracker.backend.data.db.DbConstants.TABLE_GENRES

object Genres : Table(TABLE_GENRES) {
    val id = integer("genre_id").autoIncrement()
    val nameEn = text("genre_name_en").uniqueIndex()
    val nameRu = text("genre_name_ru").uniqueIndex()

    override val primaryKey = PrimaryKey(id, name = "genres_pkey")
}

