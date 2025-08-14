package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        icon = {
            Icon(imageVector = Icons.Default.Warning, contentDescription = null)
        },
        title = {
            Text(text = "Подтвердите удаление")
        },
        text = {
            Text(text = "Вы уверены, что хотите удалить эту книгу?")
        }
    )
}
