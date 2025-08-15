package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import android.net.Uri
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormUiState

data class BookEditUiState(
    val title: String = "",
    val author: String = "",
    val coverUri: Uri? = null,
    val isSaving: Boolean = false,
    val userMessage: String? = null,
    val navigateToBookId: String? = null,
    val isLoading: Boolean = false,
    val initialCoverUrl: String? = null,
    val isDeleting: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val deletionCompleted: Boolean = false,
    val hasTitleBeenTouched: Boolean = false,
    val hasAuthorBeenTouched: Boolean = false
) : BaseBookFormUiState<BookEditUiState> {
    private val isTitleValid: Boolean
        get() = title.isNotBlank()

    private val isAuthorValid: Boolean
        get() = author.isNotBlank()

    val showTitleError: Boolean
        get() = !isTitleValid && hasTitleBeenTouched

    val showAuthorError: Boolean
        get() = !isAuthorValid && hasAuthorBeenTouched

    val isSaveButtonEnabled: Boolean
        get() = isTitleValid && isAuthorValid && !isSaving && !isLoading && !isDeleting

    override fun copyState(
        title: String?,
        author: String?,
        coverUri: Uri?,
        userMessage: String?,
        hasTitleBeenTouched: Boolean?,
        hasAuthorBeenTouched: Boolean?
    ): BookEditUiState {
        return this.copy(
            title = title ?: this.title,
            author = author ?: this.author,
            coverUri = coverUri ?: this.coverUri,
            userMessage = userMessage ?: this.userMessage,
            hasTitleBeenTouched = hasTitleBeenTouched ?: this.hasTitleBeenTouched,
            hasAuthorBeenTouched = hasAuthorBeenTouched ?: this.hasAuthorBeenTouched
        )
    }
}
