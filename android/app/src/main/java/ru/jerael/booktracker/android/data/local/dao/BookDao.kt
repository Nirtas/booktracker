package ru.jerael.booktracker.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.data.local.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<BookEntity>>
}
