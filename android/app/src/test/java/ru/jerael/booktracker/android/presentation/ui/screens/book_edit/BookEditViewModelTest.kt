package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import androidx.lifecycle.SavedStateHandle
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
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.usecases.book.DeleteBookUseCase
import ru.jerael.booktracker.android.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.book.UpdateBookUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import ru.jerael.booktracker.android.presentation.ui.util.StringResourceProvider
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class BookEditViewModelTest {

    @MockK
    private lateinit var getBookByIdUseCase: GetBookByIdUseCase

    @MockK
    private lateinit var updateBookUseCase: UpdateBookUseCase

    @MockK
    private lateinit var deleteBookUseCase: DeleteBookUseCase

    @MockK
    private lateinit var getGenresUseCase: GetGenresUseCase

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    @MockK
    private lateinit var errorHandler: ErrorHandler

    @MockK
    private lateinit var stringResourceProvider: StringResourceProvider

    private lateinit var viewModel: BookEditViewModel

    private fun createViewModel() {
        viewModel = BookEditViewModel(
            getBookByIdUseCase,
            updateBookUseCase,
            deleteBookUseCase,
            getGenresUseCase,
            savedStateHandle,
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
        genres = genres.take(2)
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
    fun `when viewModel is initialized and all useCases succeed, it should update the state with a book and genre data`() =
        runTest {
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))

            createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(genres, state.allGenres)
            assertEquals(book.title, state.title)
            assertEquals(book.author, state.author)
            assertEquals(book.coverUrl, state.initialCoverUrl)
            assertEquals(book.status, state.selectedStatus)
            assertEquals(book.genres, state.selectedGenres)
            assertNull(state.userMessage)
            assertFalse(state.isLoading)
        }

    @Test
    fun `when getGenresUseCase fails on init, it should update the state with a user message and not load the book`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getGenresUseCase.invoke() } returns flowOf(appFailure(error))
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertTrue(state.allGenres.isEmpty())
            assertTrue(state.title.isEmpty())
            assertEquals(exceptionMessage, state.userMessage)
            assertFalse(state.isLoading)
        }

    @Test
    fun `when getBookByIdUseCase fails on init, it should update the state with a user message and contain genres`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appFailure(error))
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(genres, state.allGenres)
            assertTrue(state.title.isEmpty())
            assertEquals(exceptionMessage, state.userMessage)
            assertFalse(state.isLoading)
        }

    @Test
    fun `when onSaveClick is called and updateBookUseCase succeeds, state should reflect success and navigation`() =
        runTest {
            val message = "Successful"
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { updateBookUseCase.invoke(any()) } returns appSuccess(Unit)
            every { stringResourceProvider.getString(R.string.book_updated_successfully) } returns message

            createViewModel()
            viewModel.onSaveClick()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(message, state.userMessage)
            assertEquals(bookId, state.navigateToBookId)
            assertFalse(state.isSaving)
        }

    @Test
    fun `when onSaveClick is called and updateBookUseCase fails, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { updateBookUseCase.invoke(any()) } returns appFailure(error)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            viewModel.onSaveClick()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(exceptionMessage, state.userMessage)
            assertNull(state.navigateToBookId)
            assertFalse(state.isSaving)
        }

    @Test
    fun `when onSaveClick is called with form data in the state, it should call updateBookUseCase with correctly mapped parameters`() =
        runTest {
            val message = "Successful"
            val slot = slot<BookUpdateParams>()
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { updateBookUseCase.invoke(capture(slot)) } returns appSuccess(Unit)
            every { stringResourceProvider.getString(R.string.book_updated_successfully) } returns message

            createViewModel()
            advanceUntilIdle()

            val bookUpdateParams = BookUpdateParams(
                id = bookId,
                title = book.title,
                author = book.author,
                coverUri = mockk(),
                status = book.status,
                genreIds = book.genres.map { it.id }
            )

            viewModel.onTitleChanged(bookUpdateParams.title)
            viewModel.onAuthorChanged(bookUpdateParams.author)
            viewModel.onCoverSelected(bookUpdateParams.coverUri)
            viewModel.onStatusSelected(bookUpdateParams.status)
            viewModel.onGenresSelected(book.genres)

            viewModel.onSaveClick()
            advanceUntilIdle()

            val params = slot.captured
            assertEquals(bookUpdateParams, params)
            assertFalse(viewModel.uiState.value.isSaving)
        }

    @Test
    fun `when onDeleteClick is called, state should show delete confirmation dialog`() = runTest {
        every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
        every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))

        createViewModel()
        viewModel.onDeleteClick()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showDeleteConfirmDialog)
    }

    @Test
    fun `when onDismissDeleteDialog is called, it should set showDeleteConfirmDialog to false`() =
        runTest {
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))

            createViewModel()
            viewModel.onDismissDeleteDialog()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.showDeleteConfirmDialog)
        }

    @Test
    fun `when onConfirmDelete is called and deleteBookUseCase succeeds, state should reflect successful deletion`() =
        runTest {
            val message = "Successful"
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { deleteBookUseCase.invoke(bookId) } returns appSuccess(Unit)
            every { stringResourceProvider.getString(R.string.book_deleted_successfully) } returns message

            createViewModel()
            viewModel.onConfirmDelete()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(message, state.userMessage)
            assertTrue(state.deletionCompleted)
            assertFalse(state.isDeleting)
        }

    @Test
    fun `when onConfirmDelete is called and deleteBookUseCase fails, it should update the state with a user message`() =
        runTest {
            val exceptionMessage = "Error"
            val error = AppError.UnknownError
            every { getGenresUseCase.invoke() } returns flowOf(appSuccess(genres))
            every { getBookByIdUseCase.invoke(bookId) } returns flowOf(appSuccess(book))
            coEvery { deleteBookUseCase.invoke(any()) } returns appFailure(error)
            coEvery { errorHandler.handleError(any()) } returns exceptionMessage

            createViewModel()
            viewModel.onConfirmDelete()
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(exceptionMessage, state.userMessage)
            assertFalse(state.deletionCompleted)
            assertFalse(state.isDeleting)
        }
}