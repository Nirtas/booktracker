package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.presentation.ui.model.TopBarAction
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme
import ru.jerael.booktracker.android.presentation.ui.theme.topBarTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navigationAction: TopBarAction? = null,
    actions: List<TopBarAction> = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    TopAppBar(
        title = {
            Text(text = title, style = MaterialTheme.typography.topBarTitle)
        },
        navigationIcon = {
            navigationAction?.let { action ->
                IconButton(onClick = action.onClick) {
                    Icon(imageVector = action.icon, contentDescription = action.contentDescription)
                }
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(imageVector = action.icon, contentDescription = action.contentDescription)
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun AppTopBarPreviewNoActions() {
    BookTrackerTheme {
        AppTopBar(title = "Книжная полка")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun AppTopBarPreviewWithActions() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка", actions = listOf(
                TopBarAction(
                    icon = Icons.Default.Person,
                    contentDescription = "Профиль",
                    onClick = {}
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun AppTopBarPreviewWithBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            navigationAction = TopBarAction(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                onClick = {}
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun AppTopBarPreviewWithActionsAndBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            navigationAction = TopBarAction(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                onClick = {}
            ),
            actions = listOf(
                TopBarAction(
                    icon = Icons.Default.Person,
                    contentDescription = "Профиль",
                    onClick = {}
                )
            )
        )
    }
}

