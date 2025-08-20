package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.genre.Genre

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val userMessage: String? = null,
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val activeFilters: BookListFilterState = BookListFilterState(),
    val allGenres: List<Genre> = emptyList(),
    val searchQuery: String = ""
)