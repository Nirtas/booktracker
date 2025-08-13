package ru.jerael.booktracker.android.presentation.ui.navigation

const val BOOK_LIST_SCREEN_ROUTE = "book_list_screen"
const val ADD_BOOK_SCREEN_ROUTE = "add_book_screen"

const val BOOK_DETAILS_ARG_KEY = "bookId"
const val BOOK_DETAILS_SCREEN_ROUTE = "book_details_screen/{$BOOK_DETAILS_ARG_KEY}"

sealed class Screen(val route: String) {
    object BookList : Screen(route = BOOK_LIST_SCREEN_ROUTE)
    object AddBook : Screen(route = ADD_BOOK_SCREEN_ROUTE)

    object BookDetails : Screen(route = BOOK_DETAILS_SCREEN_ROUTE) {
        fun withArgs(bookId: String): String {
            return this.route.replace("{bookId}", bookId)
        }
    }
}
