package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookFormLayout
import ru.jerael.booktracker.android.presentation.ui.components.DeleteConfirmationDialog
import ru.jerael.booktracker.android.presentation.ui.model.TopBarAction
import ru.jerael.booktracker.android.presentation.ui.model.TopBarScrollBehavior
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarType

@Composable
fun BookEditScreen(
    appViewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBookListAfterDeletion: () -> Unit
) {
    val viewModel: BookEditViewModel = hiltViewModel()
    val uiState: BookEditUiState by viewModel.uiState.collectAsState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onCoverSelected(uri = uri)
    }

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                title = "Редактирование книги",
                type = TopBarType.SMALL,
                scrollBehavior = TopBarScrollBehavior.PINNED,
                navigationAction = TopBarAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    onClick = { if (!uiState.isSaving) onNavigateBack() }
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

    LaunchedEffect(uiState.navigateToBookId) {
        uiState.navigateToBookId?.let { onNavigateBack() }
    }

    LaunchedEffect(uiState.deletionCompleted) {
        if (uiState.deletionCompleted) {
            onNavigateToBookListAfterDeletion()
        }
    }

    if (uiState.showDeleteConfirmDialog) {
        DeleteConfirmationDialog(
            onConfirm = viewModel::onConfirmDelete,
            onDismiss = viewModel::onDismissDeleteDialog
        )
    }

    BookFormLayout(
        title = uiState.title,
        isTitleValid = uiState.isTitleValid,
        onTitleChange = viewModel::onTitleChanged,
        author = uiState.author,
        isAuthorValid = uiState.isAuthorValid,
        onAuthorChange = viewModel::onAuthorChanged,
        coverModel = uiState.coverUri ?: uiState.initialCoverUrl,
        onCoverSelectClick = { imagePickerLauncher.launch("image/*") },
        isSaving = uiState.isSaving,
        isSaveButtonEnabled = uiState.isSaveButtonEnabled,
        onSaveClick = viewModel::onSaveClick,
        onCancelClick = onNavigateBack,
        onDeleteClick = viewModel::onDeleteClick
    )
}

