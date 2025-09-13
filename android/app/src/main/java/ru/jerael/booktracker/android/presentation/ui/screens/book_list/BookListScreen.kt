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

package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookCard
import ru.jerael.booktracker.android.presentation.ui.components.FilterSheet
import ru.jerael.booktracker.android.presentation.ui.components.text_fields.SearchTextField
import ru.jerael.booktracker.android.presentation.ui.model.FabAction
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
    val tempFilters by viewModel.tempFilters.collectAsState()

    LaunchedEffect(null) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                titleResId = R.string.bookshelf,
                type = TopBarType.SMALL,
                scrollBehavior = TopBarScrollBehavior.ENTER_ALWAYS
            )
        )
        appViewModel.updateFab(
            newState = FabState(
                mainAction = FabAction(
                    icon = Icons.Default.Add,
                    contentDescription = null,
                    onClick = onNavigateToAddBook
                ),
                secondaryActions = listOf(
                    FabAction(
                        icon = Icons.Default.FilterAlt,
                        contentDescription = null,
                        onClick = viewModel::onFilterButtonClick
                    )
                )
            )
        )
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            appViewModel.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    FilterSheet(
        isVisible = uiState.isFilterSheetVisible,
        onDismiss = viewModel::onFilterSheetDismiss,
        filterState = tempFilters,
        onSortByChanged = viewModel::onSortByChanged,
        onSortOrderChanged = viewModel::onSortOrderChanged,
        allGenres = uiState.allGenres,
        onGenreSelectionChanged = viewModel::onGenreSelectionChanged,
        onAppyClick = viewModel::onApplyFiltersClick,
        onResetClick = viewModel::onResetFiltersClick,
    )

    BookListScreenContent(
        uiState = uiState,
        onRefresh = { viewModel.onRefresh() },
        onBookClick = { onNavigateToBookDetails(it) },
        onSearchQueryChanged = viewModel::onSearchQueryChanged
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreenContent(
    uiState: BookListUiState,
    onRefresh: () -> Unit,
    onBookClick: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    val focusManager = LocalFocusManager.current
    PullToRefreshBox(
        state = pullRefreshState,
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.dimensions.screenPadding)
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
        ) {
            if (uiState.searchQuery != "" || uiState.books.isNotEmpty()) {
                SearchTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    text = uiState.searchQuery,
                    onTextChanged = { onSearchQueryChanged(it) },
                    onClearClick = { onSearchQueryChanged("") }
                )
            }
            when {
                uiState.isInitialLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.books.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
                        Text(text = stringResource(R.string.empty_book_list))
                    }
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
            onBookClick = {},
            onSearchQueryChanged = {}
        )
    }
}
