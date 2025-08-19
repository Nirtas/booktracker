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
import androidx.compose.material3.Button
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
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.SearchTextField

@Composable
fun GenreSelectionSheetContent(
    allGenres: List<Genre>,
    selectedGenres: List<Genre>,
    onDoneClick: (List<Genre>) -> Unit
) {
    var tempSelectedGenres by remember { mutableStateOf(selectedGenres.toSet()) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredGenres = remember(searchQuery, allGenres) {
        if (searchQuery.isBlank()) {
            allGenres
        } else {
            allGenres.filter { genre ->
                genre.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Выберите жанры",
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
            items(filteredGenres, key = { it.id }) { genre ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            tempSelectedGenres = if (tempSelectedGenres.contains(genre)) {
                                tempSelectedGenres - genre
                            } else {
                                tempSelectedGenres + genre
                            }
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = tempSelectedGenres.contains(genre),
                        onCheckedChange = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = genre.name)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onDoneClick(tempSelectedGenres.toList()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Готово")
        }
    }
}