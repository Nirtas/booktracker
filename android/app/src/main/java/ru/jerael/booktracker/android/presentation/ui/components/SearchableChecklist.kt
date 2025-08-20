package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.SearchTextField

@Composable
fun <T> SearchableChecklist(
    modifier: Modifier = Modifier,
    title: String,
    allItems: List<T>,
    selectedItems: Set<T>,
    onItemClick: (T, Boolean) -> Unit,
    itemLabel: (T) -> String
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = remember(searchQuery, allItems) {
        if (searchQuery.isBlank()) {
            allItems
        } else {
            allItems.filter { item ->
                itemLabel(item).contains(searchQuery, ignoreCase = true)
            }
        }
    }
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        SearchTextField(
            modifier = Modifier.fillMaxWidth(),
            text = searchQuery,
            onTextChanged = { searchQuery = it },
            onClearClick = { searchQuery = "" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredItems) { item ->
                val isSelected = selectedItems.contains(item)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(item, !isSelected)
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = itemLabel(item))
                }
            }
        }
    }
}