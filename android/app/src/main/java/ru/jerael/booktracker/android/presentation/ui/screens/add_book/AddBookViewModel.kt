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

package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.usecases.book.AddBookUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormViewModel
import ru.jerael.booktracker.android.presentation.ui.util.ErrorHandler
import ru.jerael.booktracker.android.presentation.ui.util.StringResourceProvider
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val errorHandler: ErrorHandler,
    private val stringResourceProvider: StringResourceProvider
) : BaseBookFormViewModel<AddBookUiState>() {

    override val _uiState: MutableStateFlow<AddBookUiState> = MutableStateFlow(AddBookUiState())

    init {
        viewModelScope.launch {
            getGenresUseCase()
                .collect { result ->
                    result
                        .onSuccess { genres ->
                            _uiState.update { it.copyState(allGenres = genres) }
                        }
                        .onFailure { throwable ->
                            _uiState.update {
                                it.copy(userMessage = errorHandler.handleError(throwable))
                            }
                        }
                }
        }
    }

    override fun onSaveClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val currentState = _uiState.value
            val bookCreationParams = BookCreationParams(
                title = currentState.title,
                author = currentState.author,
                coverUri = currentState.coverUri,
                status = currentState.selectedStatus,
                genreIds = currentState.selectedGenres.map { it.id }
            )
            addBookUseCase(bookCreationParams)
                .onSuccess { bookId ->
                    _uiState.update {
                        it.copy(
                            userMessage = stringResourceProvider.getString(R.string.book_added_successfully),
                            bookAddedSuccessfully = true,
                            createdBookId = bookId
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { it.copy(userMessage = errorHandler.handleError(throwable)) }
                }
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}