package ru.jerael.booktracker.android.presentation.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    @OptIn(ExperimentalMaterial3Api::class)
    private val _topBarState = MutableStateFlow(TopBarState(isVisible = false))
    val topBarState: StateFlow<TopBarState> = _topBarState.asStateFlow()

    fun updateTopBar(newState: TopBarState) {
        _topBarState.update { newState }
    }

    val snackbarHostState = SnackbarHostState()

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message = message)
        }
    }

    private val _fabState = MutableStateFlow<FabState?>(null)
    val fabState: StateFlow<FabState?> = _fabState.asStateFlow()

    fun updateFab(newState: FabState?) {
        _fabState.update { newState }
    }
}