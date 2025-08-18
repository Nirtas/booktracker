package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import coil3.compose.AsyncImage
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun BookCover(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    CoverContainer(modifier = modifier) {
        if (model == null) {
            Image(
                painter = painterResource(R.drawable.book_picture),
                contentScale = ContentScale.Crop,
                contentDescription = contentDescription
            )
        } else {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = model,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun BookCoverPreview() {
    BookTrackerTheme {
        BookCover(model = null, contentDescription = null)
    }
}