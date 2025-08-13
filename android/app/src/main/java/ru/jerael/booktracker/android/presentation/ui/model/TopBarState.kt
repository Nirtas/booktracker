package ru.jerael.booktracker.android.presentation.ui.model

data class TopBarState(
    val title: String = "",
    val type: TopBarType = TopBarType.SMALL,
    val scrollBehavior: TopBarScrollBehavior = TopBarScrollBehavior.PINNED,
    val navigationAction: TopBarAction? = null,
    val actions: List<TopBarAction> = emptyList()
)

enum class TopBarType {
    SMALL, MEDIUM, LARGE, CENTER_ALIGNED
}

enum class TopBarScrollBehavior {
    PINNED, ENTER_ALWAYS, EXIT_UNTIL_COLLAPSED
}