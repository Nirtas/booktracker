package ru.jerael.booktracker.android.presentation.ui.screens.book_details

import androidx.lifecycle.SavedStateHandle
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
import ru.jerael.booktracker.android.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.book.RefreshBookByIdUseCase
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailsViewModelTest {

    @MockK
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase

    @MockK
    private lateinit var refreshBookByIdUseCase: RefreshBookByIdUseCase

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var errorHandler: ErrorHandler

    private lateinit var viewModel: BookDetailsViewModel

    private fun createViewModel() {
        viewModel = BookDetailsViewModel(
            getBookByIdUseCase,
            refreshBookByIdUseCase,
            savedStateHandle,
            errorHandler
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
        every { savedStateHandle.get<String>(any()) } returns bookId
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when getBookByIdUseCase emits a failure, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appFailure(error))
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage
            coEvery { refreshBookByIdUseCase.invoke(bookId) } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                awaitItem()
                val state = awaitItem()
                assertNull(state.book)
                assertEquals(exceptionMessage, state.userMessage)
                assertFalse(state.isInitialLoading)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when viewModel is initialized with a successful refreshBookByIdUseCase result, it should update state without a user message`() =
        runTest {
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { refreshBookByIdUseCase.invoke(bookId) } returns appSuccess(Unit)

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                awaitItem()
                val state = awaitItem()
                assertNull(state.userMessage)
                assertFalse(state.isInitialLoading)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when viewModel is initialized with a failed refreshBookByIdUseCase result, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { refreshBookByIdUseCase.invoke(bookId) } returns appFailure(error)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            viewModel.uiState.test {
                awaitItem()
                val state = awaitItem()
                assertEquals(exceptionMessage, state.userMessage)
                assertFalse(state.isInitialLoading)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `when onRefresh is called with a successful result, it should show and hide the refresh indicator`() =
        runTest {
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { refreshBookByIdUseCase.invoke(bookId) } coAnswers {
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
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { refreshBookByIdUseCase.invoke(bookId) } returns appFailure(error)
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
        every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
        coEvery { refreshBookByIdUseCase.invoke(bookId) } returns appFailure(error)
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
}