package ru.jerael.booktracker.android.presentation.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewLightDark
import ru.jerael.booktracker.android.presentation.ui.model.TopBarAction
import ru.jerael.booktracker.android.presentation.ui.model.TopBarType
import ru.jerael.booktracker.android.presentation.ui.theme.BookTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    type: TopBarType = TopBarType.SMALL,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationAction: TopBarAction? = null,
    actions: List<TopBarAction> = emptyList()
) {
    val navigationIcon: @Composable () -> Unit = {
        navigationAction?.let { action ->
            IconButton(onClick = action.onClick) {
                Icon(imageVector = action.icon, contentDescription = action.contentDescription)
            }
        }
    }
    val actions: @Composable (RowScope.() -> Unit) = {
        actions.forEach { action ->
            IconButton(onClick = action.onClick) {
                Icon(imageVector = action.icon, contentDescription = action.contentDescription)
            }
        }
    }

    when (type) {
        TopBarType.SMALL -> {
            TopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        }

        TopBarType.LARGE -> {
            LargeTopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        }

        TopBarType.MEDIUM -> {
            MediumTopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        }

        TopBarType.CENTER_ALIGNED -> {
            CenterAlignedTopAppBar(
                title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = navigationIcon,
                actions = actions,
                scrollBehavior = scrollBehavior
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun SmallAppTopBarPreviewNoActions() {
    BookTrackerTheme {
        AppTopBar(title = "Книжная полка", type = TopBarType.SMALL)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun SmallAppTopBarPreviewWithActions() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            type = TopBarType.SMALL,
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun SmallAppTopBarPreviewWithBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            type = TopBarType.SMALL,
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
fun SmallAppTopBarPreviewWithActionsAndBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            type = TopBarType.SMALL,
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun MediumAppTopBarPreviewWithActionsAndBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            type = TopBarType.MEDIUM,
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun LargeAppTopBarPreviewWithActionsAndBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            type = TopBarType.LARGE,
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
fun CenterAlignedAppTopBarPreviewWithActionsAndBackButton() {
    BookTrackerTheme {
        AppTopBar(
            title = "Книжная полка",
            type = TopBarType.CENTER_ALIGNED,
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
