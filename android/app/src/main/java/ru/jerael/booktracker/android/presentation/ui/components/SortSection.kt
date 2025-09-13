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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder
import ru.jerael.booktracker.android.presentation.ui.util.toDisplayString

@Composable
fun SortSection(
    currentSortBy: SortBy,
    currentSortOrder: SortOrder,
    onSortByChanged: (SortBy) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    Column {
        Text(text = stringResource(R.string.sort_by), style = MaterialTheme.typography.titleMedium)
        SortBy.entries.forEach { sortBy ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortByChanged(sortBy) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sortBy == currentSortBy,
                    onClick = { onSortByChanged(sortBy) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = sortBy.toDisplayString())
            }
        }
        Text(
            text = stringResource(R.string.sort_order),
            style = MaterialTheme.typography.titleMedium
        )
        SortOrder.entries.forEach { sortOrder ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSortOrderChanged(sortOrder) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = sortOrder == currentSortOrder,
                    onClick = { onSortOrderChanged(sortOrder) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = sortOrder.toDisplayString())
            }
        }
    }
}