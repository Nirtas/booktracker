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