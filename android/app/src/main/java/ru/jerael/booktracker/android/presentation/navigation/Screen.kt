package ru.jerael.booktracker.android.presentation.navigation

const val BOOK_LIST_SCREEN_ROUTE = "book_list_screen"

sealed class Screen(val route: String) {
    object BookList : Screen(route = BOOK_LIST_SCREEN_ROUTE)
}
