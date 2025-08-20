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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
            text = "Сортировка и фильтры",
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
                title = "Выберите жанры",
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
                Text(text = "Сбросить")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onAppyClick) {
                Text(text = "Применить")
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