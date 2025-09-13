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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.jerael.booktracker.android.presentation.ui.components.AppScaffold
import ru.jerael.booktracker.android.presentation.ui.model.FabAction
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.navigation.AppNavHost
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun BookTrackerApp(appViewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val topBarState by appViewModel.topBarState.collectAsState()
    val fabState by appViewModel.fabState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    AppScaffold(
        topBarState = topBarState,
        snackbarHostState = appViewModel.snackbarHostState,
        fabState = fabState,
        currentRoute = currentRoute
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            appViewModel = appViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun BookTrackerAppPreview() {
    @Composable
    fun BookTrackerAppPreviewContent() {
        val topBarState = TopBarState(title = "Книжная полка")
        val snackbarHostState = SnackbarHostState()
        val fabState = FabState(
            mainAction = FabAction(
                icon = Icons.Default.Add,
                contentDescription = null,
                onClick = {}
            )
        )
        AppScaffold(
            topBarState = topBarState,
            snackbarHostState = snackbarHostState,
            fabState = fabState,
            currentRoute = null
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text("Экран")
            }
        }
    }

    BookTrackerTheme {
        BookTrackerAppPreviewContent()
    }
}