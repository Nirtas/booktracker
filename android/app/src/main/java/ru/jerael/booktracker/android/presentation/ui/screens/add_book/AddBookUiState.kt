package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import android.net.Uri

data class AddBookUiState(
    val title: String = "",
    val author: String = "",
    val coverUri: Uri? = null,
    val isTitleValid: Boolean = true,
    val isAuthorValid: Boolean = true,
    val isSaving: Boolean = false,
    val userMessage: String? = null,
    val isSaveButtonEnabled: Boolean = false,
    val bookAddedSuccessfully: Boolean = false,
    val createdBookId: String? = null
)
