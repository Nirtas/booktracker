package ru.jerael.booktracker.android.presentation.ui.screens.common

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun userMessageShown() {
        _uiState.update { it.copyState(userMessage = null) }
    }

    abstract fun onSaveClick()
}