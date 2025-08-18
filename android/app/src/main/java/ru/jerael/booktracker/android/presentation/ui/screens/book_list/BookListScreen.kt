package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookCard
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarScrollBehavior
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarType
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.dimensions
import java.time.Instant

@Composable
fun BookListScreen(
    appViewModel: AppViewModel,
    onNavigateToAddBook: () -> Unit,
    onNavigateToBookDetails: (String) -> Unit
) {
    val viewModel: BookListViewModel = hiltViewModel()
    val uiState: BookListUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                title = "Книжная полка",
                type = TopBarType.SMALL,
                scrollBehavior = TopBarScrollBehavior.ENTER_ALWAYS
            )
        )
        appViewModel.updateFab(
            newState = FabState(
                icon = Icons.Default.Add,
                contentDescription = null,
                onClick = onNavigateToAddBook
            )
        )
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            appViewModel.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    BookListScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.onRefresh() },
        onBookClick = { onNavigateToBookDetails(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreenContent(
    uiState: BookListUiState,
    onRefresh: () -> Unit,
    onBookClick: (String) -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isInitialLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.books.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.dimensions.screenPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.books) { book ->
                        BookCard(book, onBookClick)
                    }
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Список книг пуст")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BookListScreenContentPreview() {
    val books = listOf(
        Book(
            id = "1",
            title = "Название 1",
            author = "Автор 1",
            coverUrl = "http://localhost:4001/storage/covers/7e224477-0673-4cb5-8ac8-9ae99eadf7bd.jpg",
            createdAt = Instant.ofEpochMilli(0),
            status = BookStatus.WANT_TO_READ,
            genres = emptyList()
        ),
        Book(
            id = "2",
            title = "Название 2",
            author = "Автор 2",
            coverUrl = "https://cs15.pikabu.ru/post_img/2024/09/11/6/1726043826195950836.jpg",
            createdAt = Instant.ofEpochMilli(0),
            status = BookStatus.WANT_TO_READ,
            genres = emptyList()
        ),
        Book(
            id = "3",
            title = "Название 3",
            author = "Автор 3",
            coverUrl = null,
            createdAt = Instant.ofEpochMilli(0),
            status = BookStatus.WANT_TO_READ,
            genres = emptyList()
        ),
        Book(
            id = "4",
            title = "Название 4",
            author = "Автор 4",
            coverUrl = null,
            createdAt = Instant.ofEpochMilli(0),
            status = BookStatus.WANT_TO_READ,
            genres = emptyList()
        )
    )
    BookTrackerTheme {
        BookListScreenContent(
            uiState = BookListUiState(books = books),
            onRefresh = {},
            onBookClick = {}
        )
    }
}
