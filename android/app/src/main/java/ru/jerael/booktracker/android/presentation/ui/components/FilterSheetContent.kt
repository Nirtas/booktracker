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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder
import ru.jerael.booktracker.android.presentation.ui.screens.book_list.BookListFilterState
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun FilterSheetContent(
    filterState: BookListFilterState,
    onSortByChanged: (SortBy) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit,
    allGenres: List<Genre>,
    onGenreSelectionChanged: (Int, Boolean) -> Unit,
    onAppyClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.sorting_and_filters),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            SortSection(
                currentSortBy = filterState.sortBy,
                currentSortOrder = filterState.sortOrder,
                onSortByChanged = onSortByChanged,
                onSortOrderChanged = onSortOrderChanged
            )
            Spacer(modifier = Modifier.height(16.dp))
            SearchableChecklist(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.choose_genres),
                allItems = allGenres,
                selectedItems = allGenres.filter { it.id in filterState.selectedGenreIds }.toSet(),
                onItemClick = { genre, isSelected ->
                    onGenreSelectionChanged(genre.id, isSelected)
                },
                itemLabel = { it.name }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onResetClick) {
                Text(text = stringResource(R.string.reset))
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onAppyClick) {
                Text(text = stringResource(R.string.apply))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun FilterSheetContentPreview() {
    BookTrackerTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            FilterSheetContent(
                filterState = BookListFilterState(),
                onSortByChanged = {},
                onSortOrderChanged = {},
                allGenres = listOf(
                    Genre(1, "Фэнтези"),
                    Genre(2, "Научная фантастика")
                ),
                onGenreSelectionChanged = { _, _ -> },
                onAppyClick = {},
                onResetClick = {}
            )
        }
    }
}