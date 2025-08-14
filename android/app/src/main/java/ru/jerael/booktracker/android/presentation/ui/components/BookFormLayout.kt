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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.dimensions

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BookFormLayout(
    title: String,
    isTitleValid: Boolean,
    onTitleChange: (String) -> Unit,
    author: String,
    isAuthorValid: Boolean,
    onAuthorChange: (String) -> Unit,
    coverModel: Any?,
    onCoverSelectClick: () -> Unit,
    isSaving: Boolean,
    isSaveButtonEnabled: Boolean,
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
                    title = title,
                    isTitleValid = isTitleValid,
                    onTitleChange = onTitleChange,
                    author = author,
                    isAuthorValid = isAuthorValid,
                    onAuthorChange = onAuthorChange,
                    coverModel = coverModel,
                    onCoverSelectClick = onCoverSelectClick,
                    isSaving = isSaving,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCancelClick
                )
            } else {
                PortraitLayout(
                    title = title,
                    isTitleValid = isTitleValid,
                    onTitleChange = onTitleChange,
                    author = author,
                    isAuthorValid = isAuthorValid,
                    onAuthorChange = onAuthorChange,
                    coverModel = coverModel,
                    onCoverSelectClick = onCoverSelectClick,
                    isSaving = isSaving,
                    isSaveButtonEnabled = isSaveButtonEnabled,
                    onSaveClick = onSaveClick,
                    onCancelClick = onCancelClick
                )
            }
        }
    }
}

@Composable
private fun LandscapeLayout(
    title: String,
    isTitleValid: Boolean,
    onTitleChange: (String) -> Unit,
    author: String,
    isAuthorValid: Boolean,
    onAuthorChange: (String) -> Unit,
    coverModel: Any?,
    onCoverSelectClick: () -> Unit,
    isSaving: Boolean,
    isSaveButtonEnabled: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
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
                .verticalScroll(rememberScrollState())
        ) {
            BookDetailsForm(
                title = title,
                onTitleChange = onTitleChange,
                isTitleValid = isTitleValid,
                author = author,
                onAuthorChange = onAuthorChange,
                isAuthorValid = isAuthorValid,
                areFieldsEnabled = !isSaving
            )
            Spacer(modifier = Modifier.weight(1f))
            FormActionButtons(
                onSaveClick = onSaveClick,
                isSaveButtonEnabled = isSaveButtonEnabled,
                onCancelClick = onCancelClick,
                isCancelButtonEnabled = !isSaving
            )
        }
    }
}

@Composable
private fun PortraitLayout(
    title: String,
    isTitleValid: Boolean,
    onTitleChange: (String) -> Unit,
    author: String,
    isAuthorValid: Boolean,
    onAuthorChange: (String) -> Unit,
    coverModel: Any?,
    onCoverSelectClick: () -> Unit,
    isSaving: Boolean,
    isSaveButtonEnabled: Boolean,
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
                model = coverModel,
                contentDescription = null,
                onClick = onCoverSelectClick,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(0.75f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1.6f)) {
                BookDetailsForm(
                    title = title,
                    onTitleChange = onTitleChange,
                    isTitleValid = isTitleValid,
                    author = author,
                    onAuthorChange = onAuthorChange,
                    isAuthorValid = isAuthorValid,
                    areFieldsEnabled = !isSaving
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        FormActionButtons(
            onSaveClick = onSaveClick,
            isSaveButtonEnabled = isSaveButtonEnabled,
            onCancelClick = onCancelClick,
            isCancelButtonEnabled = !isSaving
        )
    }
}

@Preview(device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(device = Devices.TABLET, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LandscapeLayoutPreview() {
    BookTrackerTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            PortraitLayout(
                title = "Название",
                isTitleValid = true,
                onTitleChange = {},
                author = "Автор",
                isAuthorValid = false,
                onAuthorChange = {},
                coverModel = null,
                onCoverSelectClick = {},
                isSaving = false,
                isSaveButtonEnabled = false,
                onSaveClick = {},
                onCancelClick = {}
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
                isTitleValid = true,
                onTitleChange = {},
                author = "Автор",
                isAuthorValid = false,
                onAuthorChange = {},
                coverModel = null,
                onCoverSelectClick = {},
                isSaving = false,
                isSaveButtonEnabled = false,
                onSaveClick = {},
                onCancelClick = {}
            )
        }
    }
}
