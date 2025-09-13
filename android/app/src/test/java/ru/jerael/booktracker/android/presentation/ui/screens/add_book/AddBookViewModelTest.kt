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

package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.appFailure
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.usecases.book.AddBookUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import ru.jerael.booktracker.android.presentation.ui.util.StringResourceProvider
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class AddBookViewModelTest {

    @MockK
    private lateinit var addBookUseCase: AddBookUseCase

    @MockK
    private lateinit var getGenresUseCase: GetGenresUseCase

    @MockK
    private lateinit var errorHandler: ErrorHandler

    @MockK
    private lateinit var stringResourceProvider: StringResourceProvider

    private lateinit var viewModel: AddBookViewModel

    private fun createViewModel() {
        viewModel = AddBookViewModel(
            addBookUseCase,
            getGenresUseCase,
            errorHandler,
            stringResourceProvider
        )
    }

    private val genres = listOf(
        Genre(id = 1, name = "gaming"),
        Genre(id = 2, name = "adventure"),
        Genre(id = 3, name = "science fiction")
    )

    private val bookId = "e607ac1d-309f-4301-b71f-52b59e7cb4db"
    private val book = Book(
        id = bookId,
        title = "Title",
        author = "Author",
        coverUrl = null,
        status = BookStatus.WANT_TO_READ,
        createdAt = Instant.now(),
        genres = genres
    )

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
    fun `when viewModel is initialized with a successful getGenresUseCase result, it should update the state with the list of genres`() =
        runTest {
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))

            createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(genres, state.allGenres)
            assertNull(state.userMessage)
        }

    @Test
    fun `when viewModel is initialized with a failed getGenresUseCase result, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getGenresUseCase.invoke() } returns flowOf(appFailure(error))
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.allGenres.isEmpty())
            assertEquals(exceptionMessage, state.userMessage)
        }

    @Test
    fun `when onSaveClick is called with valid data and a successful addBookUseCase result, it should update the state to success`() =
        runTest {
            val message = "Successful"
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(emptyList()))
            coEvery { addBookUseCase.invoke(any()) } returns appSuccess(bookId)
            every { stringResourceProvider.getString(R.string.book_added_successfully) } returns message

            createViewModel()
            viewModel.onSaveClick()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(message, state.userMessage)
            assertTrue(state.bookAddedSuccessfully)
            assertEquals(bookId, state.createdBookId)
            assertFalse(state.isSaving)
        }

    @Test
    fun `when onSaveClick is called with valid data and a failed addBookUseCase result, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(emptyList()))
            coEvery { addBookUseCase.invoke(any()) } returns appFailure(error)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            viewModel.onSaveClick()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(exceptionMessage, state.userMessage)
            assertFalse(state.isSaving)
        }

    @Test
    fun `when onSaveClick is called with form data in the state, it should call addBookUseCase with correctly mapped parameters`() =
        runTest {
            val message = "Successful"
            val slot = slot<BookCreationParams>()
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(emptyList()))
            coEvery { addBookUseCase.invoke(capture(slot)) } returns appSuccess(bookId)
            every { stringResourceProvider.getString(R.string.book_added_successfully) } returns message

            createViewModel()
            advanceUntilIdle()

            val bookCreationParams = BookCreationParams(
                title = book.title,
                author = book.author,
                coverUri = mockk(),
                status = book.status,
                genreIds = book.genres.map { it.id }
            )

            viewModel.onTitleChanged(bookCreationParams.title)
            viewModel.onAuthorChanged(bookCreationParams.author)
            viewModel.onCoverSelected(bookCreationParams.coverUri)
            viewModel.onStatusSelected(bookCreationParams.status)
            viewModel.onGenresSelected(book.genres)

            viewModel.onSaveClick()
            advanceUntilIdle()

            val params = slot.captured
            assertEquals(bookCreationParams, params)
            assertFalse(viewModel.uiState.value.isSaving)
        }
}