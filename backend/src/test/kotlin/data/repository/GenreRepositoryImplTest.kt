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

package data.repository

import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.repository.GenreRepositoryImpl
import ru.jerael.booktracker.backend.domain.exceptions.GenreNotFoundException
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenreRepositoryImplTest : RepositoryTestBase() {

    private val genreRepository: GenreRepository = GenreRepositoryImpl()

    private data class GenreItem(
        val id: Int,
        val nameEn: String,
        val nameRu: String
    )

    private val genres = listOf(
        GenreItem(id = 1, nameEn = "gaming", nameRu = "игры"),
        GenreItem(id = 2, nameEn = "adventure", nameRu = "приключения"),
        GenreItem(id = 3, nameEn = "science fiction", nameRu = "научная фантастика")
    )

    @BeforeEach
    fun setUp() {
        transaction {
            Genres.deleteAll()
            Genres.batchInsert(genres) { genre ->
                this[Genres.id] = genre.id
                this[Genres.nameEn] = genre.nameEn
                this[Genres.nameRu] = genre.nameRu
            }
        }
    }

    @Test
    fun `when getGenres is called with 'en' language, it should return a list of genres sorted by their English names`() =
        runTest {
            val expectedNames = genres.map { it.nameEn }.sorted()

            val result = genreRepository.getGenres("en")

            val actualNames = result.map { it.name }
            assertEquals(expectedNames, actualNames)
        }

    @Test
    fun `when getGenres is called with 'ru' language, it should return a list of genres sorted by their Russian names`() =
        runTest {
            val expectedNames = genres.map { it.nameRu }.sorted()

            val result = genreRepository.getGenres("ru")

            val actualNames = result.map { it.name }
            assertEquals(expectedNames, actualNames)
        }

    @Test
    fun `when getGenres is called and the genres table is empty, it should return an empty list`() = runTest {
        transaction {
            Genres.deleteAll()
        }

        val result = genreRepository.getGenres("en")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when getGenreById is called with an existing ID and 'ru' language, it should return the correct genre with its Russian name`() =
        runTest {
            val genreId = 1
            val expectedName = genres.find { it.id == genreId }!!.nameRu

            val result = genreRepository.getGenreById(genreId, "ru")

            assertEquals(expectedName, result.name)
        }

    @Test
    fun `when getGenreById is called with a non-existent ID, a GenreNotFoundException should be thrown`() =
        runTest {
            val genreId = 5

            val exception = assertThrows<GenreNotFoundException> {
                genreRepository.getGenreById(genreId, "ru")
            }

            assertTrue(exception.message!!.contains("$genreId"))
        }

    @Test
    fun `when getGenresByIds is called with a list of existing IDs, it should return a list of genres sorted by their English names`() =
        runTest {
            val genreIds = listOf(3, 1)
            val expectedNames = genres.filter { it.id in genreIds }.map { it.nameEn }.sorted()

            val result = genreRepository.getGenresByIds(genreIds, "en")

            val actualNames = result.map { it.name }
            assertEquals(expectedNames, actualNames)
        }

    @Test
    fun `when getGenresByIds is called with a mix of existing and non-existent IDs, it should return only the existing genres`() =
        runTest {
            val genreIds = listOf(5, 3, 1)
            val expectedNames = genres.filter { it.id in genreIds }.map { it.nameEn }.sorted()

            val result = genreRepository.getGenresByIds(genreIds, "en")

            val actualNames = result.map { it.name }
            assertEquals(expectedNames, actualNames)
        }

    @Test
    fun `when getGenresByIds is called with a list of non-existent IDs, it should return an empty list`() = runTest {
        val genreIds = listOf(5, 6, 7)

        val result = genreRepository.getGenresByIds(genreIds, "en")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `when getGenresByIds is called with an empty ID list, it should return an empty list`() = runTest {
        val genreIds = emptyList<Int>()

        val result = genreRepository.getGenresByIds(genreIds, "en")

        assertTrue(result.isEmpty())
    }
}