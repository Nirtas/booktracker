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
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder
import ru.jerael.booktracker.android.presentation.ui.model.toDisplayString

@Composable
fun SortSection(
    currentSortBy: SortBy,
    currentSortOrder: SortOrder,
    onSortByChanged: (SortBy) -> Unit,
    onSortOrderChanged: (SortOrder) -> Unit
) {
    Column {
        Text(text = "Сортировать по", style = MaterialTheme.typography.titleMedium)
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
        Text(text = "Порядок", style = MaterialTheme.typography.titleMedium)
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