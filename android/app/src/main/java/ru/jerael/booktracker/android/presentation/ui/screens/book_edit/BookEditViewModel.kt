package ru.jerael.booktracker.android.presentation.ui.screens.book_edit

import android.app.Application
import android.net.Uri
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.domain.model.BookUpdatePayload
import ru.jerael.booktracker.android.domain.usecases.DeleteBookUseCase
import ru.jerael.booktracker.android.domain.usecases.GetBookByIdUseCase
import ru.jerael.booktracker.android.domain.usecases.UpdateBookUseCase
import ru.jerael.booktracker.android.presentation.ui.navigation.BOOK_ID_ARG_KEY
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BookEditViewModel @Inject constructor(
    getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookUseCase: UpdateBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _bookId: String = checkNotNull(savedStateHandle[BOOK_ID_ARG_KEY])
    private val _bookFlow: StateFlow<Book?> = getBookByIdUseCase(_bookId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )
    private val _hasLoadedInitialData: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _title: MutableStateFlow<String> = MutableStateFlow("")
    private val _author: MutableStateFlow<String> = MutableStateFlow("")
    private val _coverUri: MutableStateFlow<Uri?> = MutableStateFlow(null)
    private val _initialCoverUrl: StateFlow<String?> = _bookFlow.map { it?.coverUrl }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    private val formDataFlow: Flow<FormData> = combine(
        _title, _author, _coverUri, _initialCoverUrl
    ) { title, author, coverUri, initialCoverUrl ->
        FormData(
            title = title,
            author = author,
            coverUri = coverUri,
            initialCoverUrl = initialCoverUrl
        )
    }

    private val _isTitleValid: MutableStateFlow<Boolean> = MutableStateFlow(true)
    private val _isAuthorValid: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val validationStateFlow: Flow<ValidationState> = combine(
        _isTitleValid, _isAuthorValid
    ) { isTitleValid, isAuthorValid ->
        ValidationState(isTitleValid = isTitleValid, isAuthorValid = isAuthorValid)
    }

    private val _isSaving: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _userMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _navigateToBookId: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _isLoading: Flow<Boolean> = _bookFlow.map {
        it == null && !_hasLoadedInitialData.value
    }

    private val processStateFlow: Flow<ProcessState> = combine(
        _isSaving, _userMessage, _navigateToBookId, _isLoading
    ) { isSaving, userMessage, navigateToBookId, isLoading ->
        ProcessState(
            isSaving = isSaving,
            userMessage = userMessage,
            navigateToBookId = navigateToBookId,
            isLoading = isLoading
        )
    }

    private val _isDeleting: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _showDeleteConfirmDialog: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _deletionCompleted: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val deleteStateFlow: Flow<DeleteState> = combine(
        _isDeleting, _showDeleteConfirmDialog, _deletionCompleted
    ) { isDeleting, showDeleteConfirmDialog, deletionCompleted ->
        DeleteState(
            isDeleting = isDeleting,
            showDeleteConfirmDialog = showDeleteConfirmDialog,
            deletionCompleted = deletionCompleted
        )
    }

    val uiState: StateFlow<BookEditUiState> = combine(
        formDataFlow, validationStateFlow, processStateFlow, deleteStateFlow
    ) { formData, validationState, processState, deleteState ->
        val isSaveButtonEnabled = formData.title.isNotBlank() && formData.author.isNotBlank() &&
                !processState.isSaving && !processState.isLoading && !deleteState.isDeleting
        BookEditUiState(
            title = formData.title,
            author = formData.author,
            coverUri = formData.coverUri,
            isTitleValid = validationState.isTitleValid,
            isAuthorValid = validationState.isAuthorValid,
            isSaving = processState.isSaving,
            userMessage = processState.userMessage,
            isSaveButtonEnabled = isSaveButtonEnabled,
            navigateToBookId = processState.navigateToBookId,
            isLoading = processState.isLoading,
            initialCoverUrl = formData.initialCoverUrl,
            isDeleting = deleteState.isDeleting,
            showDeleteConfirmDialog = deleteState.showDeleteConfirmDialog,
            deletionCompleted = deleteState.deletionCompleted
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = BookEditUiState()
    )

    init {
        viewModelScope.launch {
            _bookFlow.collect { book ->
                if (book != null && !_hasLoadedInitialData.value) {
                    _title.value = book.title
                    _author.value = book.author
                    _hasLoadedInitialData.value = true
                }
            }
        }
    }

    fun onTitleChanged(newTitle: String) {
        _title.value = newTitle
        _isTitleValid.value = !newTitle.isBlank()
    }

    fun onAuthorChanged(newAuthor: String) {
        _author.value = newAuthor
        _isAuthorValid.value = !newAuthor.isBlank()
    }

    fun onCoverSelected(uri: Uri?) {
        _coverUri.value = uri
    }

    private fun validateInput(): Boolean {
        val isTitleValid = _title.value.isNotBlank()
        val isAuthorValid = _author.value.isNotBlank()
        _isTitleValid.value = isTitleValid
        _isAuthorValid.value = isAuthorValid
        return isTitleValid && isAuthorValid
    }

    fun onSaveClick() {
        if (!validateInput()) return
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val coverFile = getFileFromUri(_coverUri.value)
                val payload = BookUpdatePayload(
                    id = _bookId,
                    title = _title.value,
                    author = _author.value,
                    coverFile = coverFile
                )
                val result = updateBookUseCase(payload)
                if (result.isSuccess) {
                    _userMessage.value = "Книга успешно изменена"
                    _navigateToBookId.value = _bookId
                } else {
                    val exception = result.exceptionOrNull()
                    _userMessage.value = "Ошибка при изменении книги"
                    Log.e("BookEditViewModel", "Ошибка при изменении книги", exception)
                }
            } catch (e: Exception) {
                _userMessage.value = "Ошибка при изменении книги"
                Log.e("BookEditViewModel", "Ошибка при изменении книги", e)
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun onDeleteClick() {
        _showDeleteConfirmDialog.value = true
    }

    fun onDismissDeleteDialog() {
        _showDeleteConfirmDialog.value = false
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            _isDeleting.value = true
            _showDeleteConfirmDialog.value = false
            val result = deleteBookUseCase(_bookId)
            if (result.isSuccess) {
                _userMessage.value = "Книга успешно удалена"
                _deletionCompleted.value = true
            } else {
                val exception = result.exceptionOrNull()
                _userMessage.value = "Ошибка при удалении книги"
                Log.e("BookEditViewModel", "Ошибка при удалении книги", exception)
                _deletionCompleted.value = false
            }
            _isDeleting.value = false
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }

    private fun getFileFromUri(uri: Uri?): File? {
        if (uri == null) return null

        val contentResolver = application.contentResolver
        val file = File(application.cacheDir, "cover.jpg")

        contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    private data class FormData(
        val title: String,
        val author: String,
        val coverUri: Uri?,
        val initialCoverUrl: String?
    )

    private data class ValidationState(val isTitleValid: Boolean, val isAuthorValid: Boolean)

    private data class ProcessState(
        val isSaving: Boolean,
        val userMessage: String?,
        val navigateToBookId: String?,
        val isLoading: Boolean
    )

    private data class DeleteState(
        val isDeleting: Boolean,
        val showDeleteConfirmDialog: Boolean,
        val deletionCompleted: Boolean
    )
}