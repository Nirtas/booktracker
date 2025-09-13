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

package ru.jerael.booktracker.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_GENRES
import ru.jerael.booktracker.android.data.local.entity.GenreEntity

@Dao
interface GenreDao {
    @Query("SELECT * FROM $TABLE_GENRES ORDER BY name ASC")
    fun getAll(): Flow<List<GenreEntity>>

    @Upsert
    suspend fun upsertAll(genreEntities: List<GenreEntity>)

    @Transaction
    suspend fun clearAndInsert(genreEntities: List<GenreEntity>) {
        clearAll()
        upsertAll(genreEntities)
    }

    @Query("DELETE FROM $TABLE_GENRES")
    suspend fun clearAll()
}