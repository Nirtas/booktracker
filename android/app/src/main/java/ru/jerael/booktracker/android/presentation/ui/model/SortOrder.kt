package ru.jerael.booktracker.android.presentation.ui.model

enum class SortOrder {
    ASCENDING,
    DESCENDING
}

fun SortOrder.toDisplayString(): String {
    return when (this) {
        SortOrder.ASCENDING -> "По возрастанию"
        SortOrder.DESCENDING -> "По убыванию"
    }
}