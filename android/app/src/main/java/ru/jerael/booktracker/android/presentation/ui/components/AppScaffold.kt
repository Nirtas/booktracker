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

package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarScrollBehavior
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBarState: TopBarState?,
    snackbarHostState: SnackbarHostState,
    fabState: FabState?,
    currentRoute: String?,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = topBarState?.let {
        when (it.scrollBehavior) {
            TopBarScrollBehavior.PINNED -> {
                val topAppBarState = remember(currentRoute) {
                    TopAppBarState(0f, 0f, 0f)
                }
                TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
            }

            TopBarScrollBehavior.ENTER_ALWAYS -> {
                val topAppBarState = remember(currentRoute) {
                    TopAppBarState(0f, 0f, 0f)
                }
                TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
            }

            TopBarScrollBehavior.EXIT_UNTIL_COLLAPSED -> {
                val topAppBarState = remember(currentRoute) {
                    TopAppBarState(0f, 0f, 0f)
                }
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
            }
        }
    }
    Scaffold(
        modifier = if (scrollBehavior != null) {
            Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        } else {
            Modifier
        },
        topBar = {
            topBarState?.let { state ->
                val title = if (state.titleResId != null) {
                    stringResource(state.titleResId)
                } else {
                    state.title
                }
                AppTopBar(
                    title = title,
                    type = state.type,
                    scrollBehavior = scrollBehavior,
                    navigationAction = state.navigationAction,
                    actions = state.actions
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            fabState?.let { state ->
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.secondaryActions.forEach { action ->
                        SmallFloatingActionButton(
                            onClick = action.onClick,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.contentDescription
                            )
                        }
                    }
                    state.mainAction?.let { action ->
                        FloatingActionButton(onClick = action.onClick) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = action.contentDescription
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}