package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookCard
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    appViewModel: AppViewModel,
    onNavigateToAddBook: () -> Unit
) {
    val viewModel: BookListViewModel = hiltViewModel()
    val uiState: BookListUiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                title = "Книжная полка",
                isVisible = true,
                scrollBehavior = scrollBehavior
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

    BookListScreenContent(uiState = uiState, onRefresh = { viewModel.onRefresh() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreenContent(uiState: BookListUiState, onRefresh: () -> Unit) {
    val pullRefreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimensions.screenPadding),
            verticalArrangement = if (uiState.books.isNotEmpty()) {
                Arrangement.spacedBy(BookListScreenDefaults.ItemsSpacing)
            } else {
                Arrangement.Center
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                uiState.isInitialLoading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                uiState.books.isNotEmpty() -> {
                    items(uiState.books) { book ->
                        BookCard(book)
                    }
                }

                else -> {
                    item {
                        Text(text = "Список книг пуст")
                    }
                }
            }
        }
    }
}

private object BookListScreenDefaults {
    val ItemsSpacing = 16.dp
}

@PreviewLightDark
@Composable
fun BookListScreenContentPreview() {
    val books = listOf(
        Book(
            id = "1",
            title = "Название 1",
            author = "Автор 1",
            coverUrl = "https://cs15.pikabu.ru/post_img/2024/09/11/6/1726043826195950836.jpg"
        ),
        Book(
            id = "2",
            title = "Название 2",
            author = "Автор 2",
            coverUrl = "https://cs15.pikabu.ru/post_img/2024/09/11/6/1726043826195950836.jpg"
        ),
        Book(id = "3", title = "Название 3", author = "Автор 3", coverUrl = null),
        Book(id = "4", title = "Название 4", author = "Автор 4", coverUrl = null)
    )
    BookTrackerTheme {
        BookListScreenContent(
            uiState = BookListUiState(books = books),
            onRefresh = {}
        )
    }
}
