package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.domain.usecases.GetBooksUseCase
import ru.jerael.booktracker.android.domain.usecases.RefreshBooksUseCase
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    getBooksUseCase: GetBooksUseCase,
    private val refreshBooksUseCase: RefreshBooksUseCase
) : ViewModel() {

    private val _books: Flow<List<Book>> = getBooksUseCase()
    private val _userMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<BookListUiState> = combine(
        _books,
        _userMessage,
        _isLoading
    ) { books, userMessage, isLoading ->
        BookListUiState(books = books, userMessage = userMessage, isLoading = isLoading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = BookListUiState()
    )

    init {
        refreshBooks()
    }

    private fun refreshBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = refreshBooksUseCase()
            if (result.isFailure) {
                val exception = result.exceptionOrNull()
                _userMessage.value = "Ошибка при обновлении списка книг"
                Log.e("BookListViewModel", "Ошибка при обновлении списка книг", exception)
            }
            _isLoading.value = false
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }
}