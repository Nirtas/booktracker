package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    topBarState: TopBarState,
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
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}