package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun FormActionButtons(
    onSaveClick: () -> Unit,
    isSaveButtonEnabled: Boolean,
    onCancelClick: () -> Unit,
    isCancelButtonEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(enabled = isCancelButtonEnabled, onClick = onCancelClick) {
            Text(text = "Отмена")
        }
        Spacer(Modifier.width(8.dp))
        Button(enabled = isSaveButtonEnabled, onClick = onSaveClick) {
            Text(text = "Сохранить")
        }
    }
}

@PreviewLightDark
@Composable
fun FormActionButtonsPreview() {
    BookTrackerTheme {
        FormActionButtons(
            onSaveClick = {},
            isSaveButtonEnabled = true,
            onCancelClick = {},
            isCancelButtonEnabled = false
        )
    }
}