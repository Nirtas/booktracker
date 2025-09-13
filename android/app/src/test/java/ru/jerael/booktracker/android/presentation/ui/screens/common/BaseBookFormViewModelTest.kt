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

package ru.jerael.booktracker.android.presentation.ui.screens.common

import android.net.Uri
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre

private data class TestUiState(
    override val title: String = "",
    override val author: String = "",
    override val coverUri: Uri? = null,
    override val userMessage: String? = null,
    override val hasTitleBeenTouched: Boolean = false,
    override val hasAuthorBeenTouched: Boolean = false,
    override val isStatusMenuExpanded: Boolean = false,
    override val selectedStatus: BookStatus = BookStatus.WANT_TO_READ,
    override val allStatuses: List<BookStatus> = BookStatus.entries,
    override val selectedGenres: List<Genre> = emptyList(),
    override val allGenres: List<Genre> = emptyList(),
    override val isGenreBoxEditable: Boolean = false,
    override val isGenreSheetVisible: Boolean = false
) : BaseBookFormUiState<TestUiState> {
    override fun copyState(
        title: String,
        author: String,
        coverUri: Uri?,
        userMessage: String?,
        hasTitleBeenTouched: Boolean,
        hasAuthorBeenTouched: Boolean,
        isStatusMenuExpanded: Boolean,
        selectedStatus: BookStatus,
        allStatuses: List<BookStatus>,
        selectedGenres: List<Genre>,
        allGenres: List<Genre>,
        isGenreBoxEditable: Boolean,
        isGenreSheetVisible: Boolean
    ): TestUiState {
        return this.copy(
            title = title,
            author = author,
            coverUri = coverUri,
            userMessage = userMessage,
            hasTitleBeenTouched = hasTitleBeenTouched,
            hasAuthorBeenTouched = hasAuthorBeenTouched,
            isStatusMenuExpanded = isStatusMenuExpanded,
            selectedStatus = selectedStatus,
            allStatuses = allStatuses,
            selectedGenres = selectedGenres,
            allGenres = allGenres,
            isGenreBoxEditable = isGenreBoxEditable,
            isGenreSheetVisible = isGenreSheetVisible
        )
    }
}

private class TestViewModel : BaseBookFormViewModel<TestUiState>() {
    override val _uiState = MutableStateFlow(TestUiState())
    override fun onSaveClick() {}

    fun setInitState(state: TestUiState) {
        _uiState.value = state
    }
}

class BaseBookFormViewModelTest {

    private lateinit var viewModel: TestViewModel

    private val genres = listOf(
        Genre(id = 1, name = "gaming"),
        Genre(id = 2, name = "adventure"),
        Genre(id = 3, name = "science fiction")
    )

    @BeforeEach
    fun setUp() {
        viewModel = TestViewModel()
    }

    @Test
    fun `when onTitleChanged is called, it should update the title in UI state`() = runTest {
        val newTitle = "Title new"
        viewModel.onTitleChanged(newTitle)
        assertEquals(newTitle, viewModel.uiState.value.title)
    }

    @Test
    fun `when onAuthorChanged is called, it should update the author in UI state`() = runTest {
        val newAuthor = "Author new"
        viewModel.onAuthorChanged(newAuthor)
        assertEquals(newAuthor, viewModel.uiState.value.author)
    }

    @Test
    fun `when onCoverSelected is called with a new URI, it should update the coverUri in UI state`() =
        runTest {
            val newUri = mockk<Uri>()
            viewModel.onCoverSelected(newUri)
            assertEquals(newUri, viewModel.uiState.value.coverUri)
        }

    @Test
    fun `when onTitleFocusChanged is focused then blurred, it should set hasTitleBeenTouched to true`() =
        runTest {
            assertFalse(viewModel.uiState.value.hasTitleBeenTouched)

            viewModel.onTitleFocusChanged(true)
            assertFalse(viewModel.uiState.value.hasTitleBeenTouched)

            viewModel.onTitleFocusChanged(false)
            assertTrue(viewModel.uiState.value.hasTitleBeenTouched)
        }

    @Test
    fun `when onAuthorFocusChanged is focused then blurred, it should set hasAuthorBeenTouched to true`() =
        runTest {
            assertFalse(viewModel.uiState.value.hasAuthorBeenTouched)

            viewModel.onAuthorFocusChanged(true)
            assertFalse(viewModel.uiState.value.hasAuthorBeenTouched)

            viewModel.onAuthorFocusChanged(false)
            assertTrue(viewModel.uiState.value.hasAuthorBeenTouched)
        }

    @Test
    fun `when onClearTitle is called, it should clear the title and reset the touched status`() =
        runTest {
            viewModel.onTitleChanged("Title")

            viewModel.onClearTitle()

            assertEquals("", viewModel.uiState.value.title)
            assertFalse(viewModel.uiState.value.hasTitleBeenTouched)
        }

    @Test
    fun `when onClearAuthor is called, it should clear the author and reset the touched status`() =
        runTest {
            viewModel.onAuthorChanged("Author")

            viewModel.onClearAuthor()

            assertEquals("", viewModel.uiState.value.author)
            assertFalse(viewModel.uiState.value.hasAuthorBeenTouched)
        }

    @Test
    fun `when onStatusMenuExpandedChanged is called with true, it should expand the menu`() =
        runTest {
            viewModel.onStatusMenuExpandedChanged(true)
            assertTrue(viewModel.uiState.value.isStatusMenuExpanded)
        }

    @Test
    fun `when onStatusMenuExpandedChanged is called with false, it should collapse the menu`() =
        runTest {
            viewModel.onStatusMenuExpandedChanged(false)
            assertFalse(viewModel.uiState.value.isStatusMenuExpanded)
        }

    @Test
    fun `when onStatusSelected is called with a new status, it should update the selectedStatus and close the menu`() =
        runTest {
            viewModel.onStatusSelected(BookStatus.READING)
            assertEquals(BookStatus.READING, viewModel.uiState.value.selectedStatus)
            assertFalse(viewModel.uiState.value.isStatusMenuExpanded)
        }

    @Test
    fun `when onStatusMenuDismiss is called, it should close the menu`() = runTest {
        viewModel.onStatusMenuDismiss()
        assertFalse(viewModel.uiState.value.isStatusMenuExpanded)
    }

    @Test
    fun `when onAddGenreClick is called, it should show the genre sheet`() = runTest {
        viewModel.onAddGenreClick()
        assertTrue(viewModel.uiState.value.isGenreSheetVisible)
    }

    @Test
    fun `when onRemoveGenreClick is called, it should remove the specified genre from the list`() =
        runTest {
            viewModel.setInitState(TestUiState(selectedGenres = genres))

            viewModel.onRemoveGenreClick(genres.find { it.id == 2 }!!)

            assertEquals(genres.filter { it.id != 2 }, viewModel.uiState.value.selectedGenres)
        }

    @Test
    fun `when onGenresSelected is called, it should update selectedGenres and hide the sheet`() =
        runTest {
            viewModel.onGenresSelected(genres)

            assertEquals(genres, viewModel.uiState.value.selectedGenres)
            assertFalse(viewModel.uiState.value.isGenreSheetVisible)
        }

    @Test
    fun `when onGenreSheetDismiss is called, it should set isGenreSheetVisible to false`() =
        runTest {
            viewModel.onGenreSheetDismiss()
            assertFalse(viewModel.uiState.value.isGenreSheetVisible)
        }

    @Test
    fun `when userMessageShown is called, it should set userMessage to null`() = runTest {
        viewModel.setInitState(TestUiState(userMessage = "Error"))
        viewModel.userMessageShown()
        assertNull(viewModel.uiState.value.userMessage)
    }
}