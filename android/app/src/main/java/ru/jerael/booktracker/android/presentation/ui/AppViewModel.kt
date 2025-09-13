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

package ru.jerael.booktracker.android.presentation.ui

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.usecases.genre.RefreshGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val refreshGenresUseCase: RefreshGenresUseCase
) : ViewModel() {

    private val _topBarState = MutableStateFlow<TopBarState?>(null)
    val topBarState: StateFlow<TopBarState?> = _topBarState.asStateFlow()

    init {
        viewModelScope.launch {
            refreshGenresUseCase()
        }
    }

    fun updateTopBar(newState: TopBarState?) {
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