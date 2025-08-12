package ru.jerael.booktracker.android.presentation.ui.navigation

const val BOOK_LIST_SCREEN_ROUTE = "book_list_screen"
const val ADD_BOOK_SCREEN_ROUTE = "add_book_screen"

sealed class Screen(val route: String) {
    object BookList : Screen(route = BOOK_LIST_SCREEN_ROUTE)
    object AddBook : Screen(route = ADD_BOOK_SCREEN_ROUTE)
}
