package ru.jerael.booktracker.android.presentation.ui.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.AuthorTextField
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.TitleTextField
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.dimensions

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BookFormLayout(
    title: String,
    showTitleError: Boolean,
    onTitleChange: (String) -> Unit,
    onTitleFocusChanged: (Boolean) -> Unit,
    onTitleClearClick: () -> Unit,
    author: String,
    showAuthorError: Boolean,
    onAuthorChange: (String) -> Unit,
    onAuthorFocusChanged: (Boolean) -> Unit,
    onAuthorClearClick: () -> Unit,
    coverModel: Any?,
    onCoverSelectClick: () -> Unit,
    isStatusMenuExpanded: Boolean,
    selectedStatus: BookStatus,
    allStatuses: List<BookStatus>,
    onStatusMenuExpandedChanged: (Boolean) -> Unit,
    onStatusSelected: (BookStatus) -> Unit,
    onStatusMenuDismiss: () -> Unit,
    selectedGenres: List<Genre>,
    isGenreBoxEditable: Boolean,
    onAddGenreClick: () -> Unit,
    onRemoveGenreClick: (Genre) -> Unit,
    isGenreSheetVisible: Boolean,
    allGenres: List<Genre>,
    onGenresSelected: (List<Genre>) -> Unit,
    onGenreSheetDismiss: () -> Unit,
    isSaving: Boolean,
    isSaveButtonEnabled: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.dimensions.screenPadding),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints {
            if (maxWidth > maxHeight) {
                LandscapeLayout(
                    title = title,
                    showTitleError = showTitleError,
                    onTitleChange = onTitleChange,
                    onTitleFocusChanged = onTitleFocusChanged,
                    onTitleClearClick = onTitleClearClick,
                    author = author,
                    showAuthorError = showAuthorError,
                    onAuthorChange = onAuthorChange,
                    onAuthorFocusChanged = onAuthorFocusChanged,
                    onAuthorClearClick = onAuthorClearClick,
                    coverModel = coverModel,
                    onCoverSelectClick = onCoverSelectClick,
                    isSaving = isSaving,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCancelClick,
                    onDeleteClick = onDeleteClick,
                    isStatusMenuExpanded = isStatusMenuExpanded,
                    selectedStatus = selectedStatus,
                    allStatuses = allStatuses,
                    onStatusMenuExpandedChanged = onStatusMenuExpandedChanged,
                    onStatusSelected = onStatusSelected,
                    onStatusMenuDismiss = onStatusMenuDismiss,
                    selectedGenres = selectedGenres,
                    isGenreBoxEditable = isGenreBoxEditable,
                    onAddGenreClick = onAddGenreClick,
                    onRemoveGenreClick = onRemoveGenreClick
                )
            } else {
                PortraitLayout(
                    title = title,
                    showTitleError = showTitleError,
                    onTitleChange = onTitleChange,
                    onTitleFocusChanged = onTitleFocusChanged,
                    onTitleClearClick = onTitleClearClick,
                    author = author,
                    showAuthorError = showAuthorError,
                    onAuthorChange = onAuthorChange,
                    onAuthorFocusChanged = onAuthorFocusChanged,
                    onAuthorClearClick = onAuthorClearClick,
                    coverModel = coverModel,
                    onCoverSelectClick = onCoverSelectClick,
                    isSaving = isSaving,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCancelClick,
                    onDeleteClick = onDeleteClick,
                    isStatusMenuExpanded = isStatusMenuExpanded,
                    selectedStatus = selectedStatus,
                    allStatuses = allStatuses,
                    onStatusMenuExpandedChanged = onStatusMenuExpandedChanged,
                    onStatusSelected = onStatusSelected,
                    onStatusMenuDismiss = onStatusMenuDismiss,
                    selectedGenres = selectedGenres,
                    isGenreBoxEditable = isGenreBoxEditable,
                    onAddGenreClick = onAddGenreClick,
                    onRemoveGenreClick = onRemoveGenreClick
                )
            }
        }
        GenreSelectionSheet(
            isVisible = isGenreSheetVisible,
            onDismiss = onGenreSheetDismiss,
            allGenres = allGenres,
            selectedGenres = selectedGenres,
            onGenresSelected = onGenresSelected
        )
    }
}

@Composable
private fun LandscapeLayout(
    title: String,
    showTitleError: Boolean,
    onTitleChange: (String) -> Unit,
    onTitleFocusChanged: (Boolean) -> Unit,
    onTitleClearClick: () -> Unit,
    author: String,
    showAuthorError: Boolean,
    onAuthorChange: (String) -> Unit,
    onAuthorFocusChanged: (Boolean) -> Unit,
    onAuthorClearClick: () -> Unit,
    coverModel: Any?,
    onCoverSelectClick: () -> Unit,
    isStatusMenuExpanded: Boolean,
    selectedStatus: BookStatus,
    allStatuses: List<BookStatus>,
    onStatusMenuExpandedChanged: (Boolean) -> Unit,
    onStatusSelected: (BookStatus) -> Unit,
    onStatusMenuDismiss: () -> Unit,
    selectedGenres: List<Genre>,
    isGenreBoxEditable: Boolean,
    onAddGenreClick: () -> Unit,
    onRemoveGenreClick: (Genre) -> Unit,
    isSaving: Boolean,
    isSaveButtonEnabled: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        CoverPicker(
            model = coverModel,
            contentDescription = null,
            onClick = onCoverSelectClick,
            modifier = Modifier
                .weight(1.5f)
                .aspectRatio(0.75f)
        )
        Spacer(modifier = Modifier.width(36.dp))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                TitleTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            onTitleFocusChanged(focusState.isFocused)
                        },
                    title = title,
                    onTextChanged = onTitleChange,
                    onClearClick = onTitleClearClick,
                    isInvalid = showTitleError,
                    isEnabled = !isSaving
                )
                Spacer(modifier = Modifier.height(16.dp))
                AuthorTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            onAuthorFocusChanged(focusState.isFocused)
                        },
                    author = author,
                    onTextChanged = onAuthorChange,
                    onClearClick = onAuthorClearClick,
                    isInvalid = showAuthorError,
                    isEnabled = !isSaving
                )
                Spacer(modifier = Modifier.height(16.dp))
                StatusDropdownMenu(
                    isExpanded = isStatusMenuExpanded,
                    selectedStatus = selectedStatus,
                    options = allStatuses,
                    onExpandedChange = { onStatusMenuExpandedChanged(it) },
                    onStatusSelected = { onStatusSelected(it) },
                    onDismiss = { onStatusMenuDismiss() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                GenreSelectionBox(
                    modifier = Modifier.fillMaxWidth(),
                    selectedGenres = selectedGenres,
                    isEditable = isGenreBoxEditable,
                    onAddClick = onAddGenreClick,
                    onRemoveClick = {
                        onRemoveGenreClick(it)
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            FormActionButtons(
                onSaveClick = onSaveClick,
                isSaveButtonEnabled = isSaveButtonEnabled,
                onCancelClick = onCancelClick,
                isCancelButtonEnabled = !isSaving,
                onDeleteClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    title: String,
    showTitleError: Boolean,
    onTitleChange: (String) -> Unit,
    onTitleFocusChanged: (Boolean) -> Unit,
    onTitleClearClick: () -> Unit,
    author: String,
    showAuthorError: Boolean,
    onAuthorChange: (String) -> Unit,
    onAuthorFocusChanged: (Boolean) -> Unit,
    onAuthorClearClick: () -> Unit,
    coverModel: Any?,
    onCoverSelectClick: () -> Unit,
    isStatusMenuExpanded: Boolean,
    selectedStatus: BookStatus,
    allStatuses: List<BookStatus>,
    onStatusMenuExpandedChanged: (Boolean) -> Unit,
    onStatusSelected: (BookStatus) -> Unit,
    onStatusMenuDismiss: () -> Unit,
    selectedGenres: List<Genre>,
    isGenreBoxEditable: Boolean,
    onAddGenreClick: () -> Unit,
    onRemoveGenreClick: (Genre) -> Unit,
    isSaving: Boolean,
    isSaveButtonEnabled: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                CoverPicker(
                    model = coverModel,
                    contentDescription = null,
                    onClick = onCoverSelectClick,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.75f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1.6f)) {
                    TitleTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                onTitleFocusChanged(focusState.isFocused)
                            },
                        title = title,
                        onTextChanged = onTitleChange,
                        onClearClick = onTitleClearClick,
                        isInvalid = showTitleError,
                        isEnabled = !isSaving
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthorTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                onAuthorFocusChanged(focusState.isFocused)
                            },
                        author = author,
                        onTextChanged = onAuthorChange,
                        onClearClick = onAuthorClearClick,
                        isInvalid = showAuthorError,
                        isEnabled = !isSaving
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            StatusDropdownMenu(
                isExpanded = isStatusMenuExpanded,
                selectedStatus = selectedStatus,
                options = allStatuses,
                onExpandedChange = { onStatusMenuExpandedChanged(it) },
                onStatusSelected = { onStatusSelected(it) },
                onDismiss = { onStatusMenuDismiss() }
            )
            Spacer(modifier = Modifier.height(16.dp))
            GenreSelectionBox(
                modifier = Modifier.fillMaxWidth(),
                selectedGenres = selectedGenres,
                isEditable = isGenreBoxEditable,
                onAddClick = onAddGenreClick,
                onRemoveClick = {
                    onRemoveGenreClick(it)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FormActionButtons(
            onSaveClick = onSaveClick,
            isSaveButtonEnabled = isSaveButtonEnabled,
            onCancelClick = onCancelClick,
            isCancelButtonEnabled = !isSaving,
            onDeleteClick = onDeleteClick
        )
    }
}

@Preview(device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LandscapeLayoutPreview() {
    BookTrackerTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            LandscapeLayout(
                title = "Название",
                showTitleError = true,
                onTitleChange = {},
                onTitleFocusChanged = {},
                onTitleClearClick = {},
                author = "Автор",
                showAuthorError = false,
                onAuthorChange = {},
                onAuthorFocusChanged = {},
                onAuthorClearClick = {},
                coverModel = null,
                onCoverSelectClick = {},
                isSaving = false,
                isSaveButtonEnabled = false,
                onSaveClick = {},
                onCancelClick = {},
                onDeleteClick = {},
                isStatusMenuExpanded = true,
                selectedStatus = BookStatus.WANT_TO_READ,
                allStatuses = BookStatus.entries,
                onStatusMenuExpandedChanged = {},
                onStatusSelected = {},
                onStatusMenuDismiss = {},
                selectedGenres = emptyList(),
                isGenreBoxEditable = true,
                onAddGenreClick = {},
                onRemoveGenreClick = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PortraitLayoutPreview() {
    BookTrackerTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            PortraitLayout(
                title = "Название",
                showTitleError = true,
                onTitleChange = {},
                onTitleFocusChanged = {},
                onTitleClearClick = {},
                author = "Автор",
                showAuthorError = false,
                onAuthorChange = {},
                onAuthorFocusChanged = {},
                onAuthorClearClick = {},
                coverModel = null,
                onCoverSelectClick = {},
                isSaving = false,
                isSaveButtonEnabled = false,
                onSaveClick = {},
                onCancelClick = {},
                onDeleteClick = {},
                isStatusMenuExpanded = true,
                selectedStatus = BookStatus.WANT_TO_READ,
                allStatuses = BookStatus.entries,
                onStatusMenuExpandedChanged = {},
                onStatusSelected = {},
                onStatusMenuDismiss = {},
                selectedGenres = emptyList(),
                isGenreBoxEditable = true,
                onAddGenreClick = {},
                onRemoveGenreClick = {}
            )
        }
    }
}
