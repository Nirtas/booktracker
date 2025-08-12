package ru.jerael.booktracker.android.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun CoverPicker(
    imageUri: Uri?,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CoverContainer(modifier = modifier.clickable(onClick = onClick)) {
        if (imageUri == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = contentDescription,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageUri,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@PreviewLightDark
@Composable
fun CoverPickerPreview() {
    BookTrackerTheme {
        CoverPicker(imageUri = null, contentDescription = null, onClick = {})
    }
}