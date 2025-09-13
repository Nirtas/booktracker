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

package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.usecases.book.DeleteBookUseCase
import ru.jerael.booktracker.android.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.book.UpdateBookUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.navigation.BOOK_ID_ARG_KEY
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormViewModel
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import ru.jerael.booktracker.android.presentation.ui.util.StringResourceProvider
import javax.inject.Inject

@HiltViewModel
class BookEditViewModel @Inject constructor(
    getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    savedStateHandle: SavedStateHandle,
    private val errorHandler: ErrorHandler,
    private val stringResourceProvider: StringResourceProvider
) : BaseBookFormViewModel<BookEditUiState>() {

    private val _bookId: String = checkNotNull(savedStateHandle[BOOK_ID_ARG_KEY])

    override val _uiState: MutableStateFlow<BookEditUiState> = MutableStateFlow(BookEditUiState())

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getGenresUseCase().first()
                .onSuccess { genres ->
                    _uiState.update { it.copyState(allGenres = genres) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            userMessage = errorHandler.handleError(throwable),
                            isLoading = false
                        )
                    }
                    return@launch
                }
            getBookByIdUseCase(_bookId).first()
                .onSuccess { book ->
                    _uiState.update {
                        it.copy(
                            title = book.title,
                            author = book.author,
                            initialCoverUrl = book.coverUrl,
                            selectedStatus = book.status,
                            selectedGenres = book.genres
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(userMessage = errorHandler.handleError(throwable)) }
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    override fun onSaveClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentState = _uiState.value
            val bookUpdateParams = BookUpdateParams(
                id = _bookId,
                title = currentState.title,
                author = currentState.author,
                coverUri = currentState.coverUri,
                status = currentState.selectedStatus,
                genreIds = currentState.selectedGenres.map { it.id }
            )
            updateBookUseCase(bookUpdateParams)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            userMessage = stringResourceProvider.getString(R.string.book_updated_successfully),
                            navigateToBookId = _bookId
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(userMessage = errorHandler.handleError(throwable)) }
                }
            _uiState.update { it.copy(isSaving = false) }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, showDeleteConfirmDialog = false) }
            deleteBookUseCase(_bookId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            userMessage = stringResourceProvider.getString(R.string.book_deleted_successfully),
                            deletionCompleted = true
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            userMessage = errorHandler.handleError(throwable),
                            deletionCompleted = false
                        )
                    }
                }
            _uiState.update { it.copy(isDeleting = false) }
        }
    }
}