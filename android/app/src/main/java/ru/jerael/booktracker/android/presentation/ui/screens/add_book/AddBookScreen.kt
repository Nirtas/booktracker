package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookDetailsForm
import ru.jerael.booktracker.android.presentation.ui.components.CoverPicker
import ru.jerael.booktracker.android.presentation.ui.components.FormActionButtons
import ru.jerael.booktracker.android.presentation.ui.model.TopBarAction
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(appViewModel: AppViewModel, onNavigateBack: () -> Unit) {
    val viewModel: AddBookViewModel = hiltViewModel()
    val uiState: AddBookUiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onCoverSelected(uri = uri)
    }

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                title = "Добавить книгу",
                isVisible = true,
                navigationAction = TopBarAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    onClick = { if (!uiState.isSaving) onNavigateBack.invoke() }
                ),
                scrollBehavior = scrollBehavior
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

    LaunchedEffect(uiState.bookAddedSuccessfully) {
        if (uiState.bookAddedSuccessfully) {
            // TODO: Navigate to BookDetailsScreen
        }
    }

    AddBookScreenContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChanged,
        onAuthorChange = viewModel::onAuthorChanged,
        onCoverSelectClick = { imagePickerLauncher.launch("image/*") },
        onSaveClick = viewModel::onSaveClick,
        onCancelClick = onNavigateBack
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AddBookScreenContent(
    uiState: AddBookUiState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onCoverSelectClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
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
                    uiState = uiState,
                    onTitleChange = onTitleChange,
                    onAuthorChange = onAuthorChange,
                    onCoverSelectClick = onCoverSelectClick,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCancelClick
                )
            } else {
                PortraitLayout(
                    uiState = uiState,
                    onTitleChange = onTitleChange,
                    onAuthorChange = onAuthorChange,
                    onCoverSelectClick = onCoverSelectClick,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCancelClick
                )
            }
        }
    }
}

private object AddBookScreenDefaults {
    val ImageToFromSpacing = 36.dp
}

@Composable
private fun LandscapeLayout(
    uiState: AddBookUiState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onCoverSelectClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.Top
    ) {
        CoverPicker(
            imageUri = uiState.coverUri,
            contentDescription = null,
            onClick = onCoverSelectClick,
            modifier = Modifier
                .weight(1.5f)
                .aspectRatio(0.75f)
        )
        Spacer(modifier = Modifier.width(AddBookScreenDefaults.ImageToFromSpacing))
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f)
                .verticalScroll(rememberScrollState())
        ) {
            BookDetailsForm(
                title = uiState.title,
                onTitleChange = onTitleChange,
                isTitleValid = uiState.isTitleValid,
                author = uiState.author,
                onAuthorChange = onAuthorChange,
                isAuthorValid = uiState.isAuthorValid,
                areFieldsEnabled = !uiState.isSaving
            )
            Spacer(modifier = Modifier.weight(1f))
            FormActionButtons(
                onSaveClick = onSaveClick,
                isSaveButtonEnabled = uiState.isSaveButtonEnabled,
                onCancelClick = onCancelClick,
                isCancelButtonEnabled = !uiState.isSaving
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    uiState: AddBookUiState,
    onTitleChange: (String) -> Unit,
    onAuthorChange: (String) -> Unit,
    onCoverSelectClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            CoverPicker(
                imageUri = uiState.coverUri,
                contentDescription = null,
                onClick = onCoverSelectClick,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.75f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1.6f)) {
                BookDetailsForm(
                    title = uiState.title,
                    onTitleChange = onTitleChange,
                    isTitleValid = uiState.isTitleValid,
                    author = uiState.author,
                    onAuthorChange = onAuthorChange,
                    isAuthorValid = uiState.isAuthorValid,
                    areFieldsEnabled = !uiState.isSaving
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        FormActionButtons(
            onSaveClick = onSaveClick,
            isSaveButtonEnabled = uiState.isSaveButtonEnabled,
            onCancelClick = onCancelClick,
            isCancelButtonEnabled = !uiState.isSaving
        )
    }
}

@Preview(device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LandscapeLayoutPreview() {
    BookTrackerTheme {
        LandscapeLayout(
            uiState = AddBookUiState(),
            onTitleChange = {},
            onAuthorChange = {},
            onCoverSelectClick = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}

@PreviewLightDark
@Composable
fun PortraitLayoutPreview() {
    BookTrackerTheme {
        PortraitLayout(
            uiState = AddBookUiState(),
            onTitleChange = {},
            onAuthorChange = {},
            onCoverSelectClick = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}
