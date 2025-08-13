package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import android.net.Uri

data class BookEditUiState(
    val title: String = "",
    val author: String = "",
    val coverUri: Uri? = null,
    val isTitleValid: Boolean = true,
    val isAuthorValid: Boolean = true,
    val isSaving: Boolean = false,
    val userMessage: String? = null,
    val isSaveButtonEnabled: Boolean = false,
    val navigateToBookId: String? = null,
    val isLoading: Boolean = false,
    val initialCoverUrl: String? = null
)
