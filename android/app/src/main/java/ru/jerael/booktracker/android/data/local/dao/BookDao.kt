package ru.jerael.booktracker.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.data.local.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAll(): Flow<List<BookEntity>>

    @Transaction
    suspend fun clearAndInsert(bookEntities: List<BookEntity>) {
        clearAll()
        upsertAll(bookEntities)
    }

    @Upsert
    suspend fun upsertAll(booksEntities: List<BookEntity>)

    @Query("DELETE FROM books")
    suspend fun clearAll()

    @Upsert
    suspend fun upsert(bookEntity: BookEntity)

    @Query("SELECT * FROM books WHERE id = :id")
    fun getBookById(id: String): Flow<BookEntity?>
}
