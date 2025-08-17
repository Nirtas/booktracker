package ru.jerael.booktracker.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.local.entity.GenreEntity

@Database(
    entities = [
        BookEntity::class,
        GenreEntity::class,
        BookGenresEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        const val DATABASE_NAME = "booktracker_db"
    }
}