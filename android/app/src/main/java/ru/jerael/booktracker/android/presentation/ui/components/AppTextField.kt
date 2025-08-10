package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    placeholder: String,
    onTextChanged: (String) -> Unit,
    isInvalid: Boolean,
    isEnabled: Boolean,
    onClearButtonClick: () -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        modifier = modifier,
        enabled = isEnabled,
        value = text,
        onValueChange = onTextChanged,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        isError = isInvalid,
        keyboardOptions = keyboardOptions,
        supportingText = {
            if (isInvalid) {
                Text(
                    text = "Введите корректное содержимое",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            if (text.isNotEmpty() && isEnabled) {
                IconButton(onClick = onClearButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
fun AppTextFieldPreview() {
    BookTrackerTheme {
        AppTextField(
            text = "Текст",
            label = "label",
            placeholder = "placeholder",
            onTextChanged = {},
            isInvalid = true,
            isEnabled = true,
            onClearButtonClick = {}
        )
    }
}