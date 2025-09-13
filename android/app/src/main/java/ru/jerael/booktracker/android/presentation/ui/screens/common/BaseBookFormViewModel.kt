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

package ru.jerael.booktracker.android.presentation.ui.screens.common

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.model.genre.Genre

abstract class BaseBookFormViewModel<T : BaseBookFormUiState<T>> : ViewModel() {
    protected abstract val _uiState: MutableStateFlow<T>
    val uiState: StateFlow<T>
        get() = _uiState.asStateFlow()

    private var _wasTitleEverFocused: Boolean = false
    private var _wasAuthorEverFocused: Boolean = false

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copyState(title = newTitle) }
    }

    fun onAuthorChanged(newAuthor: String) {
        _uiState.update { it.copyState(author = newAuthor) }
    }

    fun onCoverSelected(newUri: Uri?) {
        _uiState.update { it.copyState(coverUri = newUri) }
    }

    fun onTitleFocusChanged(isFocused: Boolean) {
        if (isFocused) _wasTitleEverFocused = true
        if (!isFocused && _wasTitleEverFocused) {
            _uiState.update { it.copyState(hasTitleBeenTouched = true) }
        }
    }

    fun onAuthorFocusChanged(isFocused: Boolean) {
        if (isFocused) _wasAuthorEverFocused = true
        if (!isFocused && _wasAuthorEverFocused) {
            _uiState.update { it.copyState(hasAuthorBeenTouched = true) }
        }
    }

    fun onClearTitle() {
        _uiState.update { it.copyState(title = "", hasTitleBeenTouched = false) }
        _wasTitleEverFocused = false
    }

    fun onClearAuthor() {
        _uiState.update { it.copyState(author = "", hasAuthorBeenTouched = false) }
        _wasAuthorEverFocused = false
    }

    fun onStatusMenuExpandedChanged(isExpanded: Boolean) {
        _uiState.update { it.copyState(isStatusMenuExpanded = isExpanded) }
    }

    fun onStatusSelected(status: BookStatus) {
        _uiState.update { it.copyState(selectedStatus = status, isStatusMenuExpanded = false) }
    }

    fun onStatusMenuDismiss() {
        _uiState.update { it.copyState(isStatusMenuExpanded = false) }
    }

    fun onAddGenreClick() {
        _uiState.update { it.copyState(isGenreSheetVisible = true) }
    }

    fun onRemoveGenreClick(genre: Genre) {
        _uiState.update { currentState ->
            val currentGenres = currentState.selectedGenres
            val newGenres = currentGenres.filter { it.id != genre.id }
            currentState.copyState(selectedGenres = newGenres)
        }
    }

    fun onGenresSelected(newSelection: List<Genre>) {
        _uiState.update {
            it.copyState(
                selectedGenres = newSelection,
                isGenreSheetVisible = false
            )
        }
    }

    fun onGenreSheetDismiss() {
        _uiState.update { it.copyState(isGenreSheetVisible = false) }
    }

    fun userMessageShown() {
        _uiState.update { it.copyState(userMessage = null) }
    }

    abstract fun onSaveClick()
}