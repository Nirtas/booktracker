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

package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import app.cash.turbine.test
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.usecases.book.GetBooksUseCase
import ru.jerael.booktracker.android.domain.usecases.book.RefreshBooksUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BookListViewModelTest {

    @MockK
    private lateinit var getBooksUseCase: GetBooksUseCase

    @MockK
    private lateinit var refreshBooksUseCase: RefreshBooksUseCase

    @MockK
    private lateinit var getGenresUseCase: GetGenresUseCase

    @MockK
    private lateinit var errorHandler: ErrorHandler

    private lateinit var viewModel: BookListViewModel

    private val firstGenre = Genre(id = 1, name = "gaming")
    private val secondGenre = Genre(id = 2, name = "adventure")
    private val thirdGenre = Genre(id = 3, name = "science fiction")

    private val genres = listOf(firstGenre, secondGenre, thirdGenre)

    private val firstBookId = "e607ac1d-309f-4301-b71f-52b59e7cb4db"
    private val firstBook = Book(
        id = firstBookId,
        title = "Title 1",
        author = "Author 1",
        coverUrl = null,
        status = BookStatus.WANT_TO_READ,
        createdAt = Instant.ofEpochMilli(1000),
        genres = listOf(firstGenre, secondGenre)
    )

    private val secondBookId = "8ec9c02f-3604-4b1a-8fdb-165b8fd6339b"
    private val secondBook = Book(
        id = secondBookId,
        title = "Title 2",
        author = "Author 2",
        coverUrl = null,
        status = BookStatus.READ,
        createdAt = Instant.ofEpochMilli(3000),
        genres = listOf(thirdGenre)
    )

    private val thirdBookId = "06d78bfc-0341-4c93-9bcb-0a0598da4789"
    private val thirdBook = Book(
        id = thirdBookId,
        title = "Title 3",
        author = "Author 3",
        coverUrl = null,
        status = BookStatus.READ,
        createdAt = Instant.ofEpochMilli(5000),
        genres = emptyList()
    )

    private val books = listOf(firstBook, secondBook, thirdBook).sortedByDescending { it.createdAt }

    private fun createViewModel() {
        viewModel = BookListViewModel(
            getBooksUseCase,
            refreshBooksUseCase,
            getGenresUseCase,
            errorHandler
        )
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getBooksUseCase emits a failure, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getBooksUseCase.invoke() } returns flowOf(appFailure(error))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                val state = awaitItem()
                assertTrue(state.books.isEmpty())
                assertEquals(exceptionMessage, state.userMessage)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when getGenresUseCase fails on init, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appFailure(error))
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                val state = awaitItem()
                assertTrue(state.allGenres.isEmpty())
                assertEquals(books, state.books)
                assertEquals(exceptionMessage, state.userMessage)
            }
        }

    @Test
    fun `when viewModel is initialized successfully, it should load books and genres into the state`() =
        runTest {
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                val state = awaitItem()
                assertFalse(state.isInitialLoading)
                assertEquals(books, state.books)
                assertEquals(genres, state.allGenres)
                assertNull(state.userMessage)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when viewModel is initialized with a failed refreshBooksUseCase, it should update state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appFailure(error)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                val state = awaitItem()
                assertEquals(exceptionMessage, state.userMessage)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when onRefresh is called with a successful result, it should show and hide the refresh indicator`() =
        runTest {
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } coAnswers {
                delay(1)
                appSuccess(Unit)
            }

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                viewModel.onRefresh()

                var state = awaitItem()
                assertTrue(state.isRefreshing)
                state = awaitItem()
                assertFalse(state.isRefreshing)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when onRefresh is called with a failed result, it should show a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appFailure(error)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                awaitItem()
                viewModel.onRefresh()
                awaitItem()

                val state = awaitItem()
                assertFalse(state.isRefreshing)
                assertEquals(exceptionMessage, state.userMessage)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when userMessageShown is called, it should set userMessage to null`() = runTest {
        val exceptionMessage = "Error"
        val error = AppError.UnknownError
        every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
        every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
        coEvery { refreshBooksUseCase.invoke() } returns appFailure(error)
        coEvery { errorHandler.handleError(any()) } returns exceptionMessage

        createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(2)
            assertEquals(exceptionMessage, viewModel.uiState.value.userMessage)

            viewModel.userMessageShown()

            val state = awaitItem()
            assertNull(state.userMessage)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when onSearchQueryChanged is called, it should filter the books list accordingly`() =
        runTest {
            val searchQuery = "Title 1"
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.onSearchQueryChanged(searchQuery)

            viewModel.uiState.test {
                skipItems(2)
                var state = awaitItem()
                assertEquals(1, state.books.size)
                assertEquals(books.find { it.title == searchQuery }, state.books.first())

                viewModel.onSearchQueryChanged("")

                state = awaitItem()
                assertEquals(books, state.books)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when onFilterButtonClick is called, it should make filter sheet visible and copy active filters to temp`() =
        runTest {
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                var state = awaitItem()
                assertFalse(state.isFilterSheetVisible)
                assertEquals(BookListFilterState(), state.activeFilters)

                viewModel.onFilterButtonClick()
                advanceUntilIdle()

                state = awaitItem()
                assertTrue(state.isFilterSheetVisible)
                assertEquals(state.activeFilters, viewModel.tempFilters.value)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when onFilterSheetDismiss is called, it should hide the filter sheet`() = runTest {
        every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
        every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
        coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(2)
            viewModel.onFilterButtonClick()

            var state = awaitItem()
            assertTrue(state.isFilterSheetVisible)

            viewModel.onFilterSheetDismiss()

            state = awaitItem()
            assertFalse(state.isFilterSheetVisible)
            assertEquals(state.activeFilters, viewModel.tempFilters.value)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when onSortByChanged is called, it should only update tempFilters`() = runTest {
        every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
        every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
        coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(2)
            viewModel.onSortByChanged(SortBy.TITLE)
            awaitItem()

            assertEquals(SortBy.TITLE, viewModel.tempFilters.value.sortBy)
            assertEquals(SortBy.DATE_ADDED, viewModel.uiState.value.activeFilters.sortBy)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when onSortOrderChanged is called, it should only update tempFilters`() = runTest {
        every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
        every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
        coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(2)
            viewModel.onSortOrderChanged(SortOrder.ASCENDING)
            awaitItem()

            assertEquals(SortOrder.ASCENDING, viewModel.tempFilters.value.sortOrder)
            assertEquals(SortOrder.DESCENDING, viewModel.uiState.value.activeFilters.sortOrder)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when onGenreSelectionChanged is called, it should add and remove genres from tempFilters`() =
        runTest {
            val genreId = firstGenre.id
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                viewModel.onGenreSelectionChanged(genreId, true)
                awaitItem()

                assertTrue(viewModel.tempFilters.value.selectedGenreIds.contains(genreId))
                assertEquals(books, viewModel.uiState.value.books)

                viewModel.onGenreSelectionChanged(genreId, false)

                assertFalse(viewModel.tempFilters.value.selectedGenreIds.contains(genreId))

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when onResetFiltersClick is called, it should clear temporary filters`() = runTest {
        val genreId = firstGenre.id
        every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
        every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
        coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.uiState.test {
            skipItems(2)
            viewModel.onFilterButtonClick()
            viewModel.onGenreSelectionChanged(genreId, true)
            awaitItem()

            assertEquals(setOf(genreId), viewModel.tempFilters.value.selectedGenreIds)

            viewModel.onResetFiltersClick()

            assertTrue(viewModel.tempFilters.value.selectedGenreIds.isEmpty())
            assertEquals(BookListFilterState(), viewModel.tempFilters.value)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `when onApplyFiltersClick is called, it should update the books list with sorted and filtered data`() =
        runTest {
            val genreId = firstGenre.id
            every { getBooksUseCase.invoke() } returns flowOf(appSuccess(books))
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            coEvery { refreshBooksUseCase.invoke() } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                skipItems(2)
                viewModel.onFilterButtonClick()
                viewModel.onGenreSelectionChanged(genreId, true)
                viewModel.onSortByChanged(SortBy.TITLE)
                viewModel.onSortOrderChanged(SortOrder.ASCENDING)
                awaitItem()

                assertEquals(books, viewModel.uiState.value.books)

                viewModel.onApplyFiltersClick()
                advanceUntilIdle()

                val state = awaitItem()
                val booksWithFirstGenre =
                    books.filter { it.genres.contains(genres.find { it.id == genreId }) }
                val expectedBooks = booksWithFirstGenre.sortedBy { it.title }
                assertFalse(state.isFilterSheetVisible)
                assertEquals(expectedBooks, state.books)
                assertEquals(setOf(genreId), state.activeFilters.selectedGenreIds)
                assertEquals(SortBy.TITLE, state.activeFilters.sortBy)
                assertEquals(SortOrder.ASCENDING, state.activeFilters.sortOrder)

                cancelAndConsumeRemainingEvents()
            }
        }
}