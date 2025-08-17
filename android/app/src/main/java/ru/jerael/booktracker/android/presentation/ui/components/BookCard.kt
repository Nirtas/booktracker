package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        onClick = { onClick.invoke(book.id) }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.width(BookCardDefaults.CoverWidth)) {
                if (book.coverUrl.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(R.drawable.book_picture),
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                } else {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = book.coverUrl,
                        contentScale = ContentScale.Crop,
                        contentDescription = null
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(BookCardDefaults.InfoPadding)
            ) {
                Text(
                    text = book.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = book.author,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
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
fun BookCardPreviewNoCover() {
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
fun BookCardPreviewWithCover() {
    BookTrackerTheme {
        BookCard(
            Book(
                id = "1",
                title = "Название книги",
                author = "Автор книги",
                coverUrl = "https://cs15.pikabu.ru/post_img/2024/09/11/6/1726043826195950836.jpg",
                createdAt = Instant.ofEpochMilli(0),
                status = BookStatus.WANT_TO_READ,
                genres = emptyList()
            ),
            {}
        )
    }
}

@PreviewLightDark
@Composable
fun BookCardPreviewTitleTwoLines() {
    BookTrackerTheme {
        BookCard(
            Book(
                id = "1",
                title = "Очень длинное название книги",
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

@PreviewLightDark
@Composable
fun BookCardPreviewTitleTooLong() {
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

