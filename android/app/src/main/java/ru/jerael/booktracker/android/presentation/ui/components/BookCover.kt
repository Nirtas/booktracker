/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.android.presentation.ui.components

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
        AsyncImage(
            model = model,
            fallback = painterResource(R.drawable.book_picture),
            error = painterResource(R.drawable.book_picture),
            contentScale = ContentScale.Crop,
            contentDescription = contentDescription
        )
    }
}

@PreviewLightDark
@Composable
private fun BookCoverPreview() {
    BookTrackerTheme {
        BookCover(model = null, contentDescription = null)
    }
}