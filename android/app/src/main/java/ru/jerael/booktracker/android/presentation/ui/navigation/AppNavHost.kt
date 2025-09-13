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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.screens.add_book.AddBookScreen
import ru.jerael.booktracker.android.presentation.ui.screens.book_details.BookDetailsScreen
import ru.jerael.booktracker.android.presentation.ui.screens.book_edit.BookEditScreen
import ru.jerael.booktracker.android.presentation.ui.screens.book_list.BookListScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screen.BookList.route
    ) {
        composable(route = Screen.BookList.route) {
            BookListScreen(
                appViewModel = appViewModel,
                onNavigateToAddBook = {
                    navController.navigate(route = Screen.AddBook.route)
                },
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(route = Screen.BookDetails.withArgs(bookId))
                }
            )
        }
        composable(route = Screen.AddBook.route) {
            AddBookScreen(
                appViewModel = appViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToBookDetails = { bookId ->
                    navController.navigate(route = Screen.BookDetails.withArgs(bookId)) {
                        popUpTo(Screen.AddBook.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = Screen.BookDetails.route,
            arguments = listOf(
                navArgument(BOOK_ID_ARG_KEY) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            BookDetailsScreen(
                appViewModel = appViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToBookEdit = { bookId ->
                    navController.navigate(route = Screen.BookEdit.withArgs(bookId))
                }
            )
        }
        composable(
            route = Screen.BookEdit.route,
            arguments = listOf(
                navArgument(BOOK_ID_ARG_KEY) {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) {
            BookEditScreen(
                appViewModel = appViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToBookListAfterDeletion = {
                    navController.navigate(route = Screen.BookList.route) {
                        popUpTo(Screen.BookList.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}