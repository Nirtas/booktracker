package ru.jerael.booktracker.android.presentation.ui.components.text_fields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.presentation.ui.components.AppTextField
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun SearchTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit,
    onClearClick: () -> Unit
) {
    AppTextField(
        modifier = modifier,
        text = text,
        label = "Поиск",
        onTextChanged = onTextChanged,
        onClearButtonClick = onClearClick,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
    )
}

@PreviewLightDark
@Composable
private fun SearchTextFieldPreview() {
    BookTrackerTheme {
        SearchTextField(
            text = "Текст",
            onTextChanged = {},
            onClearClick = {}
        )
    }
}