package ru.jerael.booktracker.android.presentation.ui.screens.common

import android.net.Uri

interface BaseBookFormUiState<T : BaseBookFormUiState<T>> {
    fun copyState(
        title: String? = null,
        author: String? = null,
        coverUri: Uri? = null,
        userMessage: String? = null,
        hasTitleBeenTouched: Boolean? = null,
        hasAuthorBeenTouched: Boolean? = null
    ): T
}
