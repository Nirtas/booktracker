package ru.jerael.booktracker.android.presentation.ui.screens.add_book

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
        viewModel.onCoverSelected(uri = uri)
    }

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                title = "Добавить книгу",
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

    BookFormLayout(
        title = uiState.title,
        isTitleValid = uiState.isTitleValid,
        onTitleChange = viewModel::onTitleChanged,
        author = uiState.author,
        isAuthorValid = uiState.isAuthorValid,
        onAuthorChange = viewModel::onAuthorChanged,
        coverModel = uiState.coverUri,
        onCoverSelectClick = { imagePickerLauncher.launch("image/*") },
        isSaving = uiState.isSaving,
        isSaveButtonEnabled = uiState.isSaveButtonEnabled,
        onSaveClick = viewModel::onSaveClick,
        onCancelClick = onNavigateBack
    )
}
