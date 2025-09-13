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

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.data.local.BookDatabase
import ru.jerael.booktracker.android.data.local.entity.GenreEntity

class GenreDaoTest {

    private lateinit var db: BookDatabase
    private lateinit var dao: GenreDao

    private val firstGenre = GenreEntity(id = 1, name = "gaming")
    private val secondGenre = GenreEntity(id = 2, name = "adventure")
    private val thirdGenre = GenreEntity(id = 3, name = "science fiction")

    private val genres = listOf(firstGenre, secondGenre, thirdGenre)

    private suspend fun initGenreTableWithTestData() {
        dao.upsertAll(genres)
    }

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BookDatabase::class.java)
            .allowMainThreadQueries().build()
        dao = db.genreDao()
    }

    @AfterEach
    fun closeDb() {
        db.close()
    }

    @Test
    fun whenTableIsEmpty_getAllReturnsFlowEmittingAnEmptyList() = runTest {
        dao.getAll().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenGenresArePresent_getAllReturnsFlowEmittingListSortedByName() = runTest {
        initGenreTableWithTestData()

        dao.getAll().test {
            val list = awaitItem()
            assertEquals(genres.sortedBy { it.name }, list)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenNewGenresAreProvided_upsertAllInsertsThemIntoTheDatabase() = runTest {
        initGenreTableWithTestData()

        val newGenres = listOf(
            GenreEntity(id = 4, name = "fantasy"),
            GenreEntity(id = 5, name = "action")
        )
        dao.upsertAll(newGenres)

        val expectedGenres = genres + newGenres

        dao.getAll().test {
            val list = awaitItem()
            assertEquals(expectedGenres.sortedBy { it.name }, list)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenGenreWithAnExistingIdIsProvided_upsertAllUpdatesItsData() = runTest {
        initGenreTableWithTestData()

        val updateGenres = listOf(
            GenreEntity(id = 1, name = "fantasy"),
            GenreEntity(id = 2, name = "action")
        )
        dao.upsertAll(updateGenres)

        val expectedGenres = listOf(
            GenreEntity(id = 1, name = "fantasy"),
            GenreEntity(id = 2, name = "action"),
            thirdGenre
        )

        dao.getAll().test {
            val list = awaitItem()
            assertEquals(expectedGenres.sortedBy { it.name }, list)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenMixOfNewAndExistingGenresIsProvided_upsertAllInsertsNewAndUpdatesExistingOnes() =
        runTest {
            initGenreTableWithTestData()

            val updateGenres = listOf(
                GenreEntity(id = 1, name = "fantasy"),
                GenreEntity(id = 4, name = "action")
            )
            dao.upsertAll(updateGenres)

            val expectedGenres = listOf(
                GenreEntity(id = 1, name = "fantasy"),
                secondGenre,
                thirdGenre,
                GenreEntity(id = 4, name = "action")
            )

            dao.getAll().test {
                val list = awaitItem()
                assertEquals(expectedGenres.sortedBy { it.name }, list)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenTableContainsData_clearAndInsertReplacesAllOldDataWithNewData() = runTest {
        initGenreTableWithTestData()

        val newGenres = listOf(
            GenreEntity(id = 4, name = "fantasy"),
            GenreEntity(id = 5, name = "action")
        )
        dao.clearAndInsert(newGenres)

        dao.getAll().test {
            val list = awaitItem()
            assertEquals(newGenres.sortedBy { it.name }, list)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenTableIsEmpty_clearAndInsertSimplyInsertsNewData() = runTest {
        val newGenres = listOf(
            GenreEntity(id = 4, name = "fantasy"),
            GenreEntity(id = 5, name = "action")
        )
        dao.clearAndInsert(newGenres)

        dao.getAll().test {
            val list = awaitItem()
            assertEquals(newGenres.sortedBy { it.name }, list)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenTableContainsData_clearAllRemovesAllEntries() = runTest {
        initGenreTableWithTestData()

        dao.clearAll()

        dao.getAll().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenTableIsEmpty_clearAllExecutesWithoutError() = runTest {
        dao.clearAll()

        dao.getAll().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }
}