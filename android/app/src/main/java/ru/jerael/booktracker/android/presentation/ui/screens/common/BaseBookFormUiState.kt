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
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre

interface BaseBookFormUiState<T : BaseBookFormUiState<T>> {

    val title: String
    val author: String
    val coverUri: Uri?
    val userMessage: String?
    val hasTitleBeenTouched: Boolean
    val hasAuthorBeenTouched: Boolean
    val isStatusMenuExpanded: Boolean
    val selectedStatus: BookStatus
    val allStatuses: List<BookStatus>
    val selectedGenres: List<Genre>
    val allGenres: List<Genre>
    val isGenreBoxEditable: Boolean
    val isGenreSheetVisible: Boolean

    fun copyState(
        title: String = this.title,
        author: String = this.author,
        coverUri: Uri? = this.coverUri,
        userMessage: String? = this.userMessage,
        hasTitleBeenTouched: Boolean = this.hasTitleBeenTouched,
        hasAuthorBeenTouched: Boolean = this.hasAuthorBeenTouched,
        isStatusMenuExpanded: Boolean = this.isStatusMenuExpanded,
        selectedStatus: BookStatus = this.selectedStatus,
        allStatuses: List<BookStatus> = this.allStatuses,
        selectedGenres: List<Genre> = this.selectedGenres,
        allGenres: List<Genre> = this.allGenres,
        isGenreBoxEditable: Boolean = this.isGenreBoxEditable,
        isGenreSheetVisible: Boolean = this.isGenreSheetVisible
    ): T
}
