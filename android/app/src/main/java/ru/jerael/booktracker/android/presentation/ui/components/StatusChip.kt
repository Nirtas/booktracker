package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.util.toDisplayString

@Composable
fun StatusChip(status: BookStatus) {
    val chipColors = when (status) {
        BookStatus.WANT_TO_READ -> {
            SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        BookStatus.READING -> {
            SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                labelColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        BookStatus.READ -> {
            SuggestionChipDefaults.suggestionChipColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                labelColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
    SuggestionChip(
        onClick = {},
        label = {
            Text(
                text = status.toDisplayString(),
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = chipColors
    )
}

@PreviewLightDark
@Composable
private fun StatusChipWantToReadPreview() {
    BookTrackerTheme {
        StatusChip(status = BookStatus.WANT_TO_READ)
    }
}

@PreviewLightDark
@Composable
private fun StatusChipReadingPreview() {
    BookTrackerTheme {
        StatusChip(status = BookStatus.READING)
    }
}

@PreviewLightDark
@Composable
private fun StatusChipReadPreview() {
    BookTrackerTheme {
        StatusChip(status = BookStatus.READ)
    }
}