/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.android.presentation.ui.navigation

const val BOOK_LIST_SCREEN_ROUTE = "book_list_screen"
const val ADD_BOOK_SCREEN_ROUTE = "add_book_screen"

const val BOOK_ID_ARG_KEY = "bookId"
const val BOOK_DETAILS_SCREEN_ROUTE = "book_details_screen/{$BOOK_ID_ARG_KEY}"
const val BOOK_EDIT_SCREEN_ROUTE = "book_edit_screen/{$BOOK_ID_ARG_KEY}"

sealed class Screen(val route: String) {
    object BookList : Screen(route = BOOK_LIST_SCREEN_ROUTE)
    object AddBook : Screen(route = ADD_BOOK_SCREEN_ROUTE)

    object BookDetails : Screen(route = BOOK_DETAILS_SCREEN_ROUTE) {
        fun withArgs(bookId: String): String {
            return this.route.replace("{$BOOK_ID_ARG_KEY}", bookId)
        }
    }

    object BookEdit : Screen(route = BOOK_EDIT_SCREEN_ROUTE) {
        fun withArgs(bookId: String): String {
            return this.route.replace("{$BOOK_ID_ARG_KEY}", bookId)
        }
    }
}
