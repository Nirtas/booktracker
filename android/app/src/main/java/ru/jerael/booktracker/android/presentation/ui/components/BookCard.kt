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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import java.time.Instant

@Composable
fun BookCard(book: Book, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(BookCardDefaults.Height),
        onClick = { onClick(book.id) }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = book.coverUrl,
                fallback = painterResource(R.drawable.book_picture),
                error = painterResource(R.drawable.book_picture),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(BookCardDefaults.CoverWidth)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(BookCardDefaults.InfoPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = book.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = book.author,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.offset(y = (-8).dp)) {
                        StatusChip(status = book.status)
                    }
                }
            }
        }
    }
}

private object BookCardDefaults {
    val Height: Dp = 128.dp
    val CoverWidth: Dp = 96.dp
    val InfoPadding: Dp = 12.dp
}

@PreviewLightDark
@Composable
private fun BookCardPreviewNoCover() {
    BookTrackerTheme {
        BookCard(
            book = Book(
                id = "1",
                title = "Название книги",
                author = "Автор книги",
                coverUrl = null,
                createdAt = Instant.ofEpochMilli(0),
                status = BookStatus.WANT_TO_READ,
                genres = emptyList()
            ),
            onClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun BookCardPreviewWithCover() {
    BookTrackerTheme {
        BookCard(
            Book(
                id = "1",
                title = "Название книги",
                author = "Автор книги",
                coverUrl = "https://cs15.pikabu.ru/post_img/2024/09/11/6/1726043826195950836.jpg",
                createdAt = Instant.ofEpochMilli(0),
                status = BookStatus.READING,
                genres = emptyList()
            ),
            {}
        )
    }
}

@PreviewLightDark
@Composable
private fun BookCardPreviewTitleTwoLines() {
    BookTrackerTheme {
        BookCard(
            Book(
                id = "1",
                title = "Очень длинное название книги",
                author = "Автор книги",
                coverUrl = null,
                createdAt = Instant.ofEpochMilli(0),
                status = BookStatus.READ,
                genres = emptyList()
            ),
            {}
        )
    }
}

@PreviewLightDark
@Composable
private fun BookCardPreviewTitleTooLong() {
    BookTrackerTheme {
        BookCard(
            Book(
                id = "1",
                title = "Слишком длинное название книги, не помещается в карточку",
                author = "Автор книги",
                coverUrl = null,
                createdAt = Instant.ofEpochMilli(0),
                status = BookStatus.WANT_TO_READ,
                genres = emptyList()
            ),
            {}
        )
    }
}

