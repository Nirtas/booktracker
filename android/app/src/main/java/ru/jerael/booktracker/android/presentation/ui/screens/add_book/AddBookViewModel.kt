package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.model.book.BookCreationParams
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.domain.usecases.book.AddBookUseCase
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormViewModel
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase
) : BaseBookFormViewModel<AddBookUiState>() {

    override val _uiState: MutableStateFlow<AddBookUiState> = MutableStateFlow(AddBookUiState())

    override fun onSaveClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val bookCreationParams = BookCreationParams(
                    title = _uiState.value.title,
                    author = _uiState.value.author,
                    coverUri = _uiState.value.coverUri,
                    status = BookStatus.WANT_TO_READ,
                    genreIds = emptyList()
                )
                val result = addBookUseCase(bookCreationParams)
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            userMessage = "Книга успешно добавлена",
                            bookAddedSuccessfully = true,
                            createdBookId = result.getOrThrow()
                        )
                    }
                } else {
                    _uiState.update { it.copy(userMessage = "Ошибка при добавлении книги") }
                    val exception = result.exceptionOrNull()
                    Log.e("AddBookViewModel", "Ошибка при добавлении книги", exception)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Ошибка при добавлении книги") }
                Log.e("AddBookViewModel", "Ошибка при добавлении книги", e)
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}