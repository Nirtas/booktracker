package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import ru.jerael.booktracker.android.domain.model.book.Book

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val userMessage: String? = null,
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false
)