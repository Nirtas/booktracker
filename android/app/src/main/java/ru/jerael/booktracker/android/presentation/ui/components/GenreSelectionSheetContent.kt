package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun GenreSelectionSheetContent(
    allGenres: List<Genre>,
    selectedGenres: List<Genre>,
    onDoneClick: (List<Genre>) -> Unit
) {
    var tempSelectedGenres by remember { mutableStateOf(selectedGenres.toSet()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SearchableChecklist(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.choose_genres),
            allItems = allGenres,
            selectedItems = tempSelectedGenres,
            onItemClick = { genre, isSelected ->
                tempSelectedGenres = if (isSelected) {
                    tempSelectedGenres + genre
                } else {
                    tempSelectedGenres - genre
                }
            },
            itemLabel = { it.name }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onDoneClick(tempSelectedGenres.toList()) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.done))
        }
    }
}

@PreviewLightDark
@Composable
private fun GenreSelectionSheetContentPreview() {
    BookTrackerTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            GenreSelectionSheetContent(
                allGenres = listOf(
                    Genre(1, "Фэнтези"),
                    Genre(2, "Научная фантастика")
                ),
                selectedGenres = listOf(Genre(1, "Фэнтези")),
                onDoneClick = {}
            )
        }
    }
}