package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import ru.jerael.booktracker.android.presentation.ui.components.BookCard

@Composable
fun BookListScreen(modifier: Modifier = Modifier) {
    val viewModel: BookListViewModel = hiltViewModel()
    val uiState: BookListUiState by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(uiState.books) { book ->
            BookCard(book = book)
        }
    }
}
