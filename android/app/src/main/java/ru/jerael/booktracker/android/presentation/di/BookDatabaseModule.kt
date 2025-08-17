package ru.jerael.booktracker.android.presentation.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.local.BookDatabase
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOKS
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_BOOK_GENRES
import ru.jerael.booktracker.android.data.local.DbConstants.TABLE_GENRES
import ru.jerael.booktracker.android.data.local.dao.BookDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookDatabaseModule {

    @Provides
    @Singleton
    fun provideBookDatabase(
        @ApplicationContext context: Context
    ): BookDatabase {
        return Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            BookDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideBookDao(database: BookDatabase): BookDao {
        return database.bookDao()
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN status TEXT NOT NULL DEFAULT 'want_to_read'")
        db.execSQL("ALTER TABLE $TABLE_BOOKS ADD COLUMN created_at INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS $TABLE_GENRES
                    (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL
                    )
                """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_genres_name ON $TABLE_GENRES (name)")
        db.execSQL(
            """
                    CREATE TABLE IF NOT EXISTS $TABLE_BOOK_GENRES
                    (
                        book_id TEXT NOT NULL,
                        genre_id INTEGER NOT NULL,
                        PRIMARY KEY (book_id, genre_id),
                        FOREIGN KEY (book_id) REFERENCES $TABLE_BOOKS (id) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY (genre_id) REFERENCES $TABLE_GENRES (id) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_book_genres_genre_id ON $TABLE_BOOK_GENRES (genre_id)")
    }
}