package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import ru.jerael.booktracker.android.domain.model.Book

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val userMessage: String? = null
)