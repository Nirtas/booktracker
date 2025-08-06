package ru.jerael.booktracker.android.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import ru.jerael.booktracker.android.presentation.ui.components.AppScaffold
import ru.jerael.booktracker.android.presentation.ui.model.TopBarState
import ru.jerael.booktracker.android.presentation.ui.navigation.AppNavHost
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@Composable
fun BookTrackerApp(appViewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val topBarState by appViewModel.topBarState.collectAsState()
    AppScaffold(
        topBarState = topBarState
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
        AppScaffold(topBarState = topBarState) { innerPadding ->
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