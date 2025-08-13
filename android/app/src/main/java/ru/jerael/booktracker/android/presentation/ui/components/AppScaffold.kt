package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarScrollBehavior
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBarState: TopBarState?,
    snackbarHostState: SnackbarHostState,
    fabState: FabState?,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = topBarState?.let {
        when (it.scrollBehavior) {
            TopBarScrollBehavior.PINNED -> {
                TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            }

            TopBarScrollBehavior.ENTER_ALWAYS -> {
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            }

            TopBarScrollBehavior.EXIT_UNTIL_COLLAPSED -> {
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
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
                AppTopBar(
                    title = state.title,
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
                FloatingActionButton(onClick = state.onClick) {
                    Icon(imageVector = state.icon, contentDescription = state.contentDescription)
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}