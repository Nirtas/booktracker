package ru.jerael.booktracker.android.presentation.ui.model

import androidx.compose.ui.graphics.vector.ImageVector

data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: String?,
    val onClick: () -> Unit
)
