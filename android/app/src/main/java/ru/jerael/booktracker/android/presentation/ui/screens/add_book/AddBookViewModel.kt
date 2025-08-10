package ru.jerael.booktracker.android.presentation.ui.screens.add_book

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBookViewModel @Inject constructor() : ViewModel() {

    private val _title: MutableStateFlow<String> = MutableStateFlow("")
    private val _author: MutableStateFlow<String> = MutableStateFlow("")
    private val _coverUri: MutableStateFlow<Uri?> = MutableStateFlow(null)

    private val formDataFlow: Flow<FormData> = combine(
        _title, _author, _coverUri
    ) { title, author, coverUri ->
        FormData(title = title, author = author, coverUri = coverUri)
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

    private val processStateFlow: Flow<ProcessState> = combine(
        _isSaving, _userMessage
    ) { isSaving, userMessage ->
        ProcessState(isSaving = isSaving, userMessage = userMessage)
    }

    val uiState: StateFlow<AddBookUiState> = combine(
        formDataFlow, validationStateFlow, processStateFlow
    ) { formData, validationState, processState ->
        val isSaveButtonEnabled =
            formData.title.isNotBlank() && formData.author.isNotBlank() && !processState.isSaving
        AddBookUiState(
            title = formData.title,
            author = formData.author,
            coverUri = formData.coverUri,
            isTitleValid = validationState.isTitleValid,
            isAuthorValid = validationState.isAuthorValid,
            isSaving = processState.isSaving,
            userMessage = processState.userMessage,
            isSaveButtonEnabled = isSaveButtonEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AddBookUiState()
    )

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
            delay(2000L)
            _isSaving.value = false
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }

    private data class FormData(val title: String, val author: String, val coverUri: Uri?)
    private data class ValidationState(val isTitleValid: Boolean, val isAuthorValid: Boolean)
    private data class ProcessState(val isSaving: Boolean, val userMessage: String?)
}