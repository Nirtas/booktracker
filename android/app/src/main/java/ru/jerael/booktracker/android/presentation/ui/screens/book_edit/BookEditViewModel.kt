package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.model.book.BookUpdateParams
import ru.jerael.booktracker.android.domain.usecases.book.DeleteBookUseCase
import ru.jerael.booktracker.android.domain.usecases.book.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.book.UpdateBookUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.navigation.BOOK_ID_ARG_KEY
import ru.jerael.booktracker.android.presentation.ui.screens.common.BaseBookFormViewModel
import javax.inject.Inject

@HiltViewModel
class BookEditViewModel @Inject constructor(
    getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    savedStateHandle: SavedStateHandle
) : BaseBookFormViewModel<BookEditUiState>() {

    private val _bookId: String = checkNotNull(savedStateHandle[BOOK_ID_ARG_KEY])

    override val _uiState: MutableStateFlow<BookEditUiState> = MutableStateFlow(BookEditUiState())

    init {
        viewModelScope.launch {
            getGenresUseCase().collect { genres ->
                _uiState.update { it.copyState(allGenres = genres) }
            }
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val book = getBookByIdUseCase(_bookId).firstOrNull()
            if (book != null) {
                _uiState.update {
                    it.copy(
                        title = book.title,
                        author = book.author,
                        initialCoverUrl = book.coverUrl,
                        selectedStatus = book.status,
                        selectedGenres = book.genres,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(userMessage = "Книга не найдена", isLoading = false) }
            }
        }
    }

    override fun onSaveClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val currentState = _uiState.value
                val bookUpdateParams = BookUpdateParams(
                    id = _bookId,
                    title = currentState.title,
                    author = currentState.author,
                    coverUri = currentState.coverUri,
                    status = currentState.selectedStatus,
                    genreIds = currentState.selectedGenres.map { it.id }
                )
                val result = updateBookUseCase(bookUpdateParams)
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            userMessage = "Книга успешно изменена",
                            navigateToBookId = _bookId
                        )
                    }
                } else {
                    _uiState.update { it.copy(userMessage = "Ошибка при изменении книги") }
                    val exception = result.exceptionOrNull()
                    Log.e("BookEditViewModel", "Ошибка при изменении книги", exception)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(userMessage = "Ошибка при изменении книги") }
                Log.e("BookEditViewModel", "Ошибка при изменении книги", e)
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
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
            val result = deleteBookUseCase(_bookId)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        userMessage = "Книга успешно удалена",
                        deletionCompleted = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        userMessage = "Ошибка при удалении книги",
                        deletionCompleted = false
                    )
                }
                val exception = result.exceptionOrNull()
                Log.e("BookEditViewModel", "Ошибка при удалении книги", exception)
            }
            _uiState.update { it.copy(isDeleting = false) }
        }
    }
}