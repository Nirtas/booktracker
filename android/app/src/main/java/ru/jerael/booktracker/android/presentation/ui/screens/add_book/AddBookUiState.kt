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

import android.net.Uri
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormUiState

data class AddBookUiState(
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
    override val isGenreBoxEditable: Boolean = true,
    override val isGenreSheetVisible: Boolean = false,
    val isSaving: Boolean = false,
    val bookAddedSuccessfully: Boolean = false,
    val createdBookId: String? = null
) : BaseBookFormUiState<AddBookUiState> {
    private val isTitleValid: Boolean
        get() = title.isNotBlank()

    private val isAuthorValid: Boolean
        get() = author.isNotBlank()

    val showTitleError: Boolean
        get() = !isTitleValid && hasTitleBeenTouched

    val showAuthorError: Boolean
        get() = !isAuthorValid && hasAuthorBeenTouched

    val isSaveButtonEnabled: Boolean
        get() = isTitleValid && isAuthorValid && !isSaving

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
    ): AddBookUiState {
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
