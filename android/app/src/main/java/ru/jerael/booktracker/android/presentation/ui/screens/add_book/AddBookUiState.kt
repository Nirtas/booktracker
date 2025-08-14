package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import android.net.Uri
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormUiState

data class AddBookUiState(
    val title: String = "",
    val author: String = "",
    val coverUri: Uri? = null,
    val isSaving: Boolean = false,
    val userMessage: String? = null,
    val bookAddedSuccessfully: Boolean = false,
    val createdBookId: String? = null,
    val hasTitleBeenTouched: Boolean = false,
    val hasAuthorBeenTouched: Boolean = false
) : BaseBookFormUiState<AddBookUiState> {
    private val isTitleValid: Boolean
        get() = title.isNotBlank()

    private val isAuthorValid: Boolean
        get() = author.isNotBlank()

    val showTitleError: Boolean
        get() = !isTitleValid && hasTitleBeenTouched

    val showAuthorError: Boolean
        get() = !isAuthorValid && hasAuthorBeenTouched

    val isSaveButtonEnabled: Boolean
        get() = isTitleValid && isAuthorValid && !isSaving

    override fun copyState(
        title: String?,
        author: String?,
        coverUri: Uri?,
        userMessage: String?,
        hasTitleBeenTouched: Boolean?,
        hasAuthorBeenTouched: Boolean?
    ): AddBookUiState {
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
