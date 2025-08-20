package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import android.net.Uri
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre
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
    val hasAuthorBeenTouched: Boolean = false,
    val isStatusMenuExpanded: Boolean = false,
    val isGenreBoxEditable: Boolean = true,
    val isGenreSheetVisible: Boolean = false,
    override val selectedStatus: BookStatus = BookStatus.WANT_TO_READ,
    override val allStatuses: List<BookStatus> = BookStatus.entries,
    override val selectedGenres: List<Genre> = emptyList(),
    override val allGenres: List<Genre> = emptyList()
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
        hasAuthorBeenTouched: Boolean?,
        isStatusMenuExpanded: Boolean?,
        selectedStatus: BookStatus?,
        allStatuses: List<BookStatus>?,
        selectedGenres: List<Genre>?,
        allGenres: List<Genre>?,
        isGenreBoxEditable: Boolean?,
        isGenreSheetVisible: Boolean?
    ): AddBookUiState {
        return this.copy(
            title = title ?: this.title,
            author = author ?: this.author,
            coverUri = coverUri ?: this.coverUri,
            userMessage = userMessage,
            hasTitleBeenTouched = hasTitleBeenTouched ?: this.hasTitleBeenTouched,
            hasAuthorBeenTouched = hasAuthorBeenTouched ?: this.hasAuthorBeenTouched,
            isStatusMenuExpanded = isStatusMenuExpanded ?: this.isStatusMenuExpanded,
            selectedStatus = selectedStatus ?: this.selectedStatus,
            allStatuses = allStatuses ?: this.allStatuses,
            selectedGenres = selectedGenres ?: this.selectedGenres,
            allGenres = allGenres ?: this.allGenres,
            isGenreBoxEditable = isGenreBoxEditable ?: this.isGenreBoxEditable,
            isGenreSheetVisible = isGenreSheetVisible ?: this.isGenreSheetVisible
        )
    }
}
