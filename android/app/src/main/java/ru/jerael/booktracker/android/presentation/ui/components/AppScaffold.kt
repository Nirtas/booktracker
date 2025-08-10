package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import ru.jerael.booktracker.android.presentation.ui.model.FabState
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBarState: TopBarState,
    snackbarHostState: SnackbarHostState,
    fabState: FabState?,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior: TopAppBarScrollBehavior? = topBarState.scrollBehavior
    Scaffold(
        modifier = if (scrollBehavior != null) Modifier.nestedScroll(scrollBehavior.nestedScrollConnection) else Modifier,
        topBar = {
            if (topBarState.isVisible) {
                AppTopBar(
                    title = topBarState.title,
                    navigationAction = topBarState.navigationAction,
                    actions = topBarState.actions,
                    scrollBehavior = scrollBehavior
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            fabState?.let {
                FloatingActionButton(onClick = it.onClick) {
                    Icon(imageVector = it.icon, contentDescription = it.contentDescription)
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}