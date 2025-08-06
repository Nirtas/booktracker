package ru.jerael.booktracker.android.presentation.ui.model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarScrollBehavior

@OptIn(ExperimentalMaterial3Api::class)
data class TopBarState(
    val title: String = "",
    val isVisible: Boolean = true,
    val navigationAction: TopBarAction? = null,
    val actions: List<TopBarAction> = emptyList(),
    val scrollBehavior: TopAppBarScrollBehavior? = null
)
