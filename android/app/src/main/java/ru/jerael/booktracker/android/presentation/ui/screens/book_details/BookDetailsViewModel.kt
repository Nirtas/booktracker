package ru.jerael.booktracker.android.presentation.ui.screens.book_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
import ru.jerael.booktracker.android.domain.usecases.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.RefreshBookByIdUseCase
import ru.jerael.booktracker.android.presentation.ui.navigation.BOOK_ID_ARG_KEY
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    getBookByIdUseCase: GetBookByIdUseCase,
    private val refreshBookByIdUseCase: RefreshBookByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _bookId: String = checkNotNull(savedStateHandle[BOOK_ID_ARG_KEY])
    private val _book: Flow<Book?> = getBookByIdUseCase(_bookId)
    private val _userMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _isInitialLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val uiState: StateFlow<BookDetailsUiState> = combine(
        _book,
        _userMessage,
        _isInitialLoading,
        _isRefreshing
    ) { book, userMessage, isInitialLoading, isRefreshing ->
        BookDetailsUiState(
            book = book,
            userMessage = userMessage,
            isInitialLoading = isInitialLoading,
            isRefreshing = isRefreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = BookDetailsUiState()
    )

    init {
        refreshBook(isPullToRefresh = false)
    }

    fun onRefresh() {
        refreshBook(isPullToRefresh = true)
    }

    private fun refreshBook(isPullToRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _isInitialLoading.value = true
            }
            val result = refreshBookByIdUseCase(_bookId)
            if (result.isFailure) {
                val exception = result.exceptionOrNull()
                _userMessage.value = "Ошибка при обновлении информации о книге"
                Log.e("BookDetailsViewModel", "Ошибка при обновлении информации о книге", exception)
            }
            _isRefreshing.value = false
            _isInitialLoading.value = false
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }
}