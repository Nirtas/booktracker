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

package ru.jerael.booktracker.android.data.repository

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.data.local.dao.GenreDao
import ru.jerael.booktracker.android.data.local.entity.GenreEntity
import ru.jerael.booktracker.android.data.mappers.GenreMapper
import ru.jerael.booktracker.android.data.remote.api.GenreApiService
import ru.jerael.booktracker.android.data.remote.dto.genre.GenreDto
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.exceptions.AppException
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.repository.GenreRepository

class GenreRepositoryImplTest {

    @MockK
    private lateinit var dao: GenreDao

    @MockK
    private lateinit var api: GenreApiService

    @MockK
    private lateinit var errorMapper: ErrorMapper

    @MockK
    private lateinit var genreMapper: GenreMapper

    private lateinit var repository: GenreRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        repository = GenreRepositoryImpl(dao, api, errorMapper, genreMapper)
    }

    @Test
    fun `when dao returns data, getGenres should emit success with mapped genres`() = runTest {
        val genres: List<GenreEntity> = listOf(mockk(), mockk())
        val mappedGenres: List<Genre> = listOf(mockk(), mockk())
        every { dao.getAll() } returns flowOf(genres)
        every { genreMapper.mapEntitiesToGenres(genres) } returns mappedGenres

        repository.getGenres().test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(mappedGenres, result.getOrNull())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when dao flow emits an error, getGenres should emit failure`() = runTest {
        val exception = RuntimeException("Error")
        val mappedError = AppError.UnknownError
        every { dao.getAll() } returns flow { throw exception }
        coEvery { errorMapper.map(exception) } returns mappedError

        repository.getGenres().test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            cancelAndConsumeRemainingEvents()
        }

        verify(exactly = 0) { genreMapper.mapEntitiesToGenres(any()) }
    }

    @Test
    fun `when api returns genres successfully, refreshGenres should save them to dao and return success`() =
        runTest {
            val genreDtos: List<GenreDto> = listOf(
                GenreDto(id = 1, name = "gaming"),
                GenreDto(id = 2, name = "adventure"),
                GenreDto(id = 3, name = "science fiction")
            )
            val mappedGenres: List<GenreEntity> = listOf(mockk())
            coEvery { api.getGenres() } returns genreDtos
            every { genreMapper.mapDtosToEntities(genreDtos) } returns mappedGenres
            coEvery { dao.clearAndInsert(mappedGenres) } just Runs

            val result = repository.refreshGenres()

            assertTrue(result.isSuccess)
            assertEquals(Unit, result.getOrNull())
        }

    @Test
    fun `when api throws an exception, refreshGenres should not access dao and return failure`() =
        runTest {
            val exception = RuntimeException("Error")
            val mappedError = AppError.UnknownError
            coEvery { api.getGenres() } throws exception
            coEvery { errorMapper.map(exception) } returns mappedError

            val result = repository.refreshGenres()

            assertTrue(result.isFailure)
            val actualException = result.exceptionOrNull()
            assertTrue(actualException is AppException)
            assertEquals(mappedError, (actualException as AppException).appError)
            coVerify(exactly = 0) { genreMapper.mapDtosToEntities(any()) }
            coVerify(exactly = 0) { dao.clearAndInsert(any()) }
        }
}