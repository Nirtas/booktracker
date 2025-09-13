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

package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.runtime.Composable
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder
import ru.jerael.booktracker.android.presentation.ui.screens.book_list.BookListFilterState

@Composable
fun FilterSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    filterState: BookListFilterState,
    onSortByChanged: (SortBy) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
    allGenres: List<Genre>,
    onGenreSelectionChanged: (Int, Boolean) -> Unit,
    onAppyClick: () -> Unit,
    onResetClick: () -> Unit
) {
    AppBottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss
    ) {
        FilterSheetContent(
            filterState = filterState,
            onSortByChanged = onSortByChanged,
            onSortOrderChanged = onSortOrderChanged,
            allGenres = allGenres,
            onGenreSelectionChanged = onGenreSelectionChanged,
            onAppyClick = onAppyClick,
            onResetClick = onResetClick
        )
    }
}