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

package ru.jerael.booktracker.android.presentation.ui.components.text_fields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.presentation.ui.components.AppTextField
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun AuthorTextField(
    modifier: Modifier = Modifier,
    author: String,
    onTextChanged: (String) -> Unit,
    onClearClick: () -> Unit,
    isInvalid: Boolean,
    isEnabled: Boolean
) {
    AppTextField(
        modifier = modifier,
        text = author,
        label = stringResource(R.string.text_field_label_author),
        onTextChanged = onTextChanged,
        isInvalid = isInvalid,
        isEnabled = isEnabled,
        onClearButtonClick = onClearClick,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        )
    )
}

@PreviewLightDark
@Composable
private fun AuthorTextFieldPreview() {
    BookTrackerTheme {
        AuthorTextField(
            author = "",
            onTextChanged = {},
            onClearClick = {},
            isInvalid = true,
            isEnabled = true
        )
    }
}