package ru.jerael.booktracker.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.jerael.booktracker.android.data.local.dao.BookDao
import ru.jerael.booktracker.android.data.local.entity.BookEntity

@Database(entities = [BookEntity::class], version = 1, exportSchema = true)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        const val DATABASE_NAME = "booktracker_db"
    }
}