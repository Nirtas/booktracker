package ru.jerael.booktracker.android.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.jerael.booktracker.android.presentation.ui.AppViewModel
import ru.jerael.booktracker.android.presentation.ui.screens.add_book.AddBookScreen
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
                }
            )
        }
        composable(route = Screen.AddBook.route) {
            AddBookScreen(
                appViewModel = appViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}