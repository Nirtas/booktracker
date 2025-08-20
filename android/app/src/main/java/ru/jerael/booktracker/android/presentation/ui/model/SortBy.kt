package ru.jerael.booktracker.android.presentation.ui.model

enum class SortBy {
    TITLE,
    AUTHOR,
    DATE_ADDED
}

fun SortBy.toDisplayString(): String {
    return when (this) {
        SortBy.TITLE -> "Названию"
        SortBy.AUTHOR -> "Автору"
        SortBy.DATE_ADDED -> "Дате добавления"
    }
}