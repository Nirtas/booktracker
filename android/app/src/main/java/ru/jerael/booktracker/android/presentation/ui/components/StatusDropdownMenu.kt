package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.presentation.ui.util.toDisplayString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdownMenu(
    isExpanded: Boolean,
    selectedStatus: BookStatus,
    options: List<BookStatus>,
    onExpandedChange: (Boolean) -> Unit,
    onStatusSelected: (BookStatus) -> Unit,
    onDismiss: () -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable, enabled = true),
            value = selectedStatus.toDisplayString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(text = stringResource(R.string.status)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            }
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismiss
        ) {
            options.forEach { status ->
                DropdownMenuItem(
                    text = { Text(text = status.toDisplayString()) },
                    onClick = {
                        onStatusSelected(status)
                    }
                )
            }
        }
    }
}