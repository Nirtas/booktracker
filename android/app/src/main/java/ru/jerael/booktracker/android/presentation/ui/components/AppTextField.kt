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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    text: String,
    label: String,
    placeholder: String = "",
    onTextChanged: (String) -> Unit,
    isInvalid: Boolean = false,
    isEnabled: Boolean = true,
    onClearButtonClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
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
                    text = stringResource(R.string.text_field_error),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (trailingIcon != null) {
                trailingIcon
            } else {
                if (text.isNotEmpty() && isEnabled && onClearButtonClick != null) {
                    IconButton(onClick = onClearButtonClick) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    )
}

@PreviewLightDark
@Composable
private fun AppTextFieldPreview() {
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