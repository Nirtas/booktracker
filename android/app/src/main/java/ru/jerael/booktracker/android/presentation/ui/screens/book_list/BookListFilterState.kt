package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder

data class BookListFilterState(
    val sortBy: SortBy = SortBy.DATE_ADDED,
    val sortOrder: SortOrder = SortOrder.DESCENDING,
    val selectedGenreIds: Set<Int> = emptySet()
)
