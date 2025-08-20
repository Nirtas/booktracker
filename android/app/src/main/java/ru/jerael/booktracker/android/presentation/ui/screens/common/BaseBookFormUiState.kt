package ru.jerael.booktracker.android.presentation.ui.screens.common

import android.net.Uri
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre

interface BaseBookFormUiState<T : BaseBookFormUiState<T>> {

    val selectedStatus: BookStatus
    val allStatuses: List<BookStatus>
    val selectedGenres: List<Genre>
    val allGenres: List<Genre>

    fun copyState(
        title: String? = null,
        author: String? = null,
        coverUri: Uri? = null,
        userMessage: String? = null,
        hasTitleBeenTouched: Boolean? = null,
        hasAuthorBeenTouched: Boolean? = null,
        isStatusMenuExpanded: Boolean? = null,
        selectedStatus: BookStatus? = null,
        allStatuses: List<BookStatus>? = null,
        selectedGenres: List<Genre>? = null,
        allGenres: List<Genre>? = null,
        isGenreBoxEditable: Boolean? = null,
        isGenreSheetVisible: Boolean? = null
    ): T
}
