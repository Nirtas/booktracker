/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun FormActionButtons(
    onSaveClick: () -> Unit,
    isSaveButtonEnabled: Boolean,
    onCancelClick: () -> Unit,
    isCancelButtonEnabled: Boolean,
    onDeleteClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onDeleteClick != null) {
            TextButton(
                onClick = onDeleteClick,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = stringResource(R.string.delete))
            }
            Spacer(Modifier.weight(1f))
        }
        TextButton(enabled = isCancelButtonEnabled, onClick = onCancelClick) {
            Text(text = stringResource(R.string.cancel))
        }
        Spacer(Modifier.width(8.dp))
        Button(enabled = isSaveButtonEnabled, onClick = onSaveClick) {
            Text(text = stringResource(R.string.save))
        }
    }
}

@PreviewLightDark
@Composable
private fun FormActionButtonsPreview() {
    BookTrackerTheme {
        FormActionButtons(
            onSaveClick = {},
            isSaveButtonEnabled = true,
            onCancelClick = {},
            isCancelButtonEnabled = false,
            onDeleteClick = {}
        )
    }
}