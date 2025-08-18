package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.runtime.Composable
import ru.jerael.booktracker.android.domain.model.genre.Genre

@Composable
fun GenreSelectionSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    allGenres: List<Genre>,
    selectedGenres: List<Genre>,
    onGenresSelected: (List<Genre>) -> Unit
) {
    AppBottomSheet(
        isVisible = isVisible,
        onDismissRequest = onDismiss
    ) {
        GenreSelectionSheetContent(
            allGenres = allGenres,
            selectedGenres = selectedGenres,
            onDoneClick = onGenresSelected
        )
    }
}