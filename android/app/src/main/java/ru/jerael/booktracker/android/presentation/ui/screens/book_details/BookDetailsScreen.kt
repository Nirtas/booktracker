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

package ru.jerael.booktracker.android.presentation.ui.screens.book_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import ru.jerael.booktracker.android.presentation.ui.components.BookCover
import ru.jerael.booktracker.android.presentation.ui.components.GenreSelectionBox
import ru.jerael.booktracker.android.presentation.ui.components.StatusChip
import ru.jerael.booktracker.android.presentation.ui.model.TopBarAction
import ru.jerael.booktracker.android.presentation.ui.model.TopBarScrollBehavior
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarType
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.dimensions
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    appViewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBookEdit: (String) -> Unit
) {
    val viewModel: BookDetailsViewModel = hiltViewModel()
    val uiState: BookDetailsUiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.book) {
        appViewModel.updateTopBar(
            newState = TopBarState(
                title = if (uiState.book != null) uiState.book!!.title else "",
                type = TopBarType.SMALL,
                scrollBehavior = TopBarScrollBehavior.ENTER_ALWAYS,
                navigationAction = TopBarAction(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    onClick = onNavigateBack
                ),
                actions = listOf(
                    TopBarAction(
                        icon = Icons.Default.Edit,
                        contentDescription = null,
                        onClick = { onNavigateToBookEdit.invoke(uiState.book!!.id) }
                    )
                )
            )
        )
        appViewModel.updateFab(newState = null)
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            appViewModel.showSnackbar(message)
            viewModel.userMessageShown()
        }
    }

    BookDetailsScreenContent(uiState = uiState, onRefresh = { viewModel.onRefresh() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreenContent(uiState: BookDetailsUiState, onRefresh: () -> Unit) {
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

            uiState.book != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(MaterialTheme.dimensions.screenPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BookCover(
                        model = uiState.book.coverUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(192.dp)
                            .aspectRatio(0.75f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = uiState.book.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.book.author,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusChip(status = uiState.book.status)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    GenreSelectionBox(selectedGenres = uiState.book.genres)
                }
            }

            uiState.userMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = uiState.userMessage)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BookDetailsScreenContentPreview() {
    val book = Book(
        id = "1",
        title = "Название 1",
        author = "Автор 1",
        coverUrl = "http://localhost:4001/storage/covers/7e224477-0673-4cb5-8ac8-9ae99eadf7bd.jpg",
        createdAt = Instant.ofEpochMilli(0),
        status = BookStatus.WANT_TO_READ,
        genres = emptyList()
    )
    BookTrackerTheme {
        Surface(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            BookDetailsScreenContent(BookDetailsUiState(book = book), onRefresh = {})
        }
    }
}
