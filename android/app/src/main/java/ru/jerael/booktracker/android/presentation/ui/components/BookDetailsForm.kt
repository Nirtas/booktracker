package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.AuthorTextField
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.TitleTextField
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun BookDetailsForm(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    isTitleValid: Boolean,
    author: String,
    onAuthorChange: (String) -> Unit,
    isAuthorValid: Boolean,
    areFieldsEnabled: Boolean
) {
    Column(modifier = modifier) {
        TitleTextField(
            title = title,
            onTextChanged = onTitleChange,
            isInvalid = !isTitleValid,
            isEnabled = areFieldsEnabled
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthorTextField(
            author = author,
            onTextChanged = onAuthorChange,
            isInvalid = !isAuthorValid,
            isEnabled = areFieldsEnabled
        )
    }
}

@PreviewLightDark
@Composable
fun BookDetailsFormPreview() {
    BookTrackerTheme {
        BookDetailsForm(
            title = "",
            onTitleChange = {},
            isTitleValid = false,
            author = "",
            onAuthorChange = {},
            isAuthorValid = true,
            areFieldsEnabled = true
        )
    }
}