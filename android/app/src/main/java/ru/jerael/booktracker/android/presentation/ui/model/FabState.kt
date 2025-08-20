package ru.jerael.booktracker.android.presentation.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class FabState(
    val mainAction: FabAction? = null,
    val secondaryActions: List<FabAction> = emptyList()
)

data class FabAction(
    val icon: ImageVector,
    val contentDescription: String?,
    val onClick: () -> Unit
)
