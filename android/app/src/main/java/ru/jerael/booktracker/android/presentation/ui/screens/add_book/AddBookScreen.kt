package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookFormLayout
import ru.jerael.booktracker.android.presentation.ui.model.TopBarAction
import ru.jerael.booktracker.android.presentation.ui.model.TopBarScrollBehavior
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarType

@Composable
fun AddBookScreen(
    appViewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBookDetails: (String) -> Unit
) {
    val viewModel: AddBookViewModel = hiltViewModel()
    val uiState: AddBookUiState by viewModel.uiState.collectAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onCoverSelected(newUri = uri)
    }

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                titleResId = R.string.add_book,
                type = TopBarType.SMALL,
                scrollBehavior = TopBarScrollBehavior.PINNED,
                navigationAction = TopBarAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    onClick = { if (!uiState.isSaving) onNavigateBack.invoke() }
                )
            )
        )
        appViewModel.updateFab(newState = null)
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            appViewModel.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    LaunchedEffect(uiState.bookAddedSuccessfully, uiState.createdBookId) {
        if (uiState.bookAddedSuccessfully && uiState.createdBookId != null) {
            onNavigateToBookDetails.invoke(uiState.createdBookId!!)
        }
    }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) {
        BookFormLayout(
            title = uiState.title,
            showTitleError = uiState.showTitleError,
            onTitleChange = viewModel::onTitleChanged,
            onTitleFocusChanged = viewModel::onTitleFocusChanged,
            onTitleClearClick = viewModel::onClearTitle,
            author = uiState.author,
            showAuthorError = uiState.showAuthorError,
            onAuthorChange = viewModel::onAuthorChanged,
            onAuthorFocusChanged = viewModel::onAuthorFocusChanged,
            onAuthorClearClick = viewModel::onClearAuthor,
            coverModel = uiState.coverUri,
            onCoverSelectClick = { imagePickerLauncher.launch("image/*") },
            isSaving = uiState.isSaving,
            isSaveButtonEnabled = uiState.isSaveButtonEnabled,
            onSaveClick = viewModel::onSaveClick,
            onCancelClick = onNavigateBack,
            isStatusMenuExpanded = uiState.isStatusMenuExpanded,
            selectedStatus = uiState.selectedStatus,
            allStatuses = uiState.allStatuses,
            onStatusMenuExpandedChanged = viewModel::onStatusMenuExpandedChanged,
            onStatusSelected = viewModel::onStatusSelected,
            onStatusMenuDismiss = viewModel::onStatusMenuDismiss,
            selectedGenres = uiState.selectedGenres,
            isGenreBoxEditable = uiState.isGenreBoxEditable,
            onAddGenreClick = viewModel::onAddGenreClick,
            onRemoveGenreClick = viewModel::onRemoveGenreClick,
            isGenreSheetVisible = uiState.isGenreSheetVisible,
            allGenres = uiState.allGenres,
            onGenresSelected = viewModel::onGenresSelected,
            onGenreSheetDismiss = viewModel::onGenreSheetDismiss
        )
    }
}
