package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.Image
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
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    CoverContainer(modifier = modifier) {
        if (imageUrl.isNullOrBlank()) {
            Image(
                painter = painterResource(R.drawable.book_picture),
                contentScale = ContentScale.Crop,
                contentDescription = contentDescription
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@PreviewLightDark
@Composable
fun BookCoverPreview() {
    BookTrackerTheme {
        BookCover(imageUrl = null, contentDescription = null)
    }
}