package ru.jerael.booktracker.android.presentation.ui.screens.book_details

import ru.jerael.booktracker.android.domain.model.Book

data class BookDetailsUiState(
    val book: Book? = null,
    val userMessage: String? = null,
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false
)