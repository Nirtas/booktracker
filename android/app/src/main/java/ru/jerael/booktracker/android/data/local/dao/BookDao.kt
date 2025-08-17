package ru.jerael.booktracker.android.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOKS
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOK_GENRES
import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres

@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM $TABLE_BOOKS")
    fun getBookWithGenres(): Flow<List<BookWithGenres>>

    @Transaction
    @Query("SELECT * FROM $TABLE_BOOKS WHERE id = :id")
    fun getBookWithGenresById(id: String): Flow<BookWithGenres?>

    @Upsert
    suspend fun upsertBook(bookEntity: BookEntity)

    @Upsert
    suspend fun upsertBookGenres(bookGenresEntities: List<BookGenresEntity>)

    @Upsert
    suspend fun upsertBookWithGenres(
        bookEntity: BookEntity,
        bookGenresEntities: List<BookGenresEntity>
    ) {
        upsertBook(bookEntity)
        deleteBookGenres(bookEntity.id)
        upsertBookGenres(bookGenresEntities)
    }

    @Query("DELETE FROM $TABLE_BOOK_GENRES WHERE book_id = :id")
    suspend fun deleteBookGenres(id: String)

    @Query("DELETE FROM $TABLE_BOOKS WHERE id = :id")
    suspend fun deleteBookById(id: String)
}
