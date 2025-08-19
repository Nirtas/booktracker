package ru.jerael.booktracker.android.presentation.ui.components.text_fields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.presentation.ui.components.AppTextField
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun TitleTextField(
    modifier: Modifier = Modifier,
    title: String,
    onTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    isInvalid: Boolean,
    isEnabled: Boolean
) {
    AppTextField(
        modifier = modifier,
        text = title,
        label = "Название книги *",
        onTextChanged = onTextChanged,
        isInvalid = isInvalid,
        isEnabled = isEnabled,
        onClearButtonClick = onClearClick,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    )
}

@PreviewLightDark
@Composable
private fun TitleTextFieldPreview() {
    BookTrackerTheme {
        TitleTextField(
            title = "",
            onTextChanged = {},
            onClearClick = {},
            isInvalid = true,
            isEnabled = true
        )
    }
}