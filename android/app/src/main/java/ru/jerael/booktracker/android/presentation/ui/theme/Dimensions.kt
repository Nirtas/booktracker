package ru.jerael.booktracker.android.presentation.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Dimensions(
    val screenPadding: Dp = 16.dp
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }