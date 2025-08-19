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