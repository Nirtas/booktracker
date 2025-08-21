package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun GenreSelectionBox(
    modifier: Modifier = Modifier,
    selectedGenres: List<Genre>,
    isEditable: Boolean = false,
    onAddClick: (() -> Unit)? = null,
    onRemoveClick: ((Genre) -> Unit)? = null
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.genres),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedGenres.isEmpty() && !isEditable) {
                    Text(text = stringResource(R.string.not_specified))
                }
                selectedGenres.sortedBy { it.name }.forEach { genre ->
                    InputChip(
                        selected = false,
                        onClick = { if (isEditable) onRemoveClick?.invoke(genre) },
                        label = { Text(text = genre.name) },
                        trailingIcon = {
                            if (isEditable) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(InputChipDefaults.IconSize)
                                )
                            }
                        }
                    )
                }
                if (isEditable) {
                    IconButton(onClick = { onAddClick?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun GenreSelectionBoxPreview() {
    val selectedGenres = listOf(
        Genre(1, "Фэнтези"),
        Genre(2, "Научная фантастика"),
        Genre(3, "Ужасы"),
        Genre(4, "Приключения"),
        Genre(5, "Очень длинное название, непонятно что и зачем оно надо вообще")
    )
    BookTrackerTheme {
        GenreSelectionBox(
            selectedGenres = selectedGenres,
            isEditable = true,
            onAddClick = null,
            onRemoveClick = null
        )
    }
}