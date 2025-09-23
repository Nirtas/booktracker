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

package domain.validation

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.GenresNotFoundException
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.domain.validation.GenreValidator

class GenreValidatorTest {

    @MockK
    private lateinit var genreRepository: GenreRepository

    private lateinit var validator: GenreValidator

    private val language = "en"
    private val foundGenres = listOf(
        Genre(1, "genre 1"),
        Genre(2, "genre 2"),
        Genre(3, "genre 3")
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        validator = GenreValidator(genreRepository)
    }

    @Test
    fun `when genreIds list is empty, the validation should complete without errors and not call repository`() =
        runTest {
            val genreIds: List<Int> = emptyList()

            assertDoesNotThrow {
                validator.invoke(genreIds, language)
            }

            coVerify(exactly = 0) { genreRepository.getGenresByIds(any(), any()) }
        }

    @Test
    fun `when all genres exist, the validation should complete without errors`() = runTest {
        val genreIds: List<Int> = listOf(1, 2, 3, 2)
        val distinctIds: List<Int> = genreIds.distinct()
        coEvery { genreRepository.getGenresByIds(distinctIds, language) } returns foundGenres

        assertDoesNotThrow {
            validator.invoke(genreIds, language)
        }

        coVerify(exactly = 1) { genreRepository.getGenresByIds(distinctIds, language) }
    }

    @Test
    fun `when one or more genres are not found, a GenresNotFoundException should be thrown`() = runTest {
        val genreIds: List<Int> = listOf(1, 2, 3, 2, 4)
        val distinctIds: List<Int> = genreIds.distinct()
        coEvery { genreRepository.getGenresByIds(distinctIds, language) } returns foundGenres

        val exception = assertThrows<GenresNotFoundException> {
            validator.invoke(genreIds, language)
        }

        assertTrue(exception.genreIds.contains(4))
        coVerify(exactly = 1) { genreRepository.getGenresByIds(distinctIds, language) }
    }
}