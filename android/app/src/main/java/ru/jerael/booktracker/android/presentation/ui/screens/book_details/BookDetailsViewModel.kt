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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.book.RefreshBookByIdUseCase
import ru.jerael.booktracker.android.presentation.ui.navigation.BOOK_ID_ARG_KEY
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import javax.inject.Inject

@HiltViewModel
class BookDetailsViewModel @Inject constructor(
    getBookByIdUseCase: GetBookByIdUseCase,
    private val refreshBookByIdUseCase: RefreshBookByIdUseCase,
    savedStateHandle: SavedStateHandle,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private var _bookId: String = checkNotNull(savedStateHandle[BOOK_ID_ARG_KEY])
    private val _book: Flow<Book?> = getBookByIdUseCase(_bookId)
        .map { result ->
            result.getOrElse { throwable ->
                _userMessage.value = errorHandler.handleError(throwable)
                null
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

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
            refreshBookByIdUseCase(_bookId)
                .onFailure { throwable ->
                    _userMessage.value = errorHandler.handleError(throwable)
                }
            _isRefreshing.value = false
            _isInitialLoading.value = false
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }
}