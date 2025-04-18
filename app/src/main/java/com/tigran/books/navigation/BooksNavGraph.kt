package com.tigran.books.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tigran.books.ui.screen.BookDetailsScreen
import com.tigran.books.ui.screen.SearchScreen
import com.tigran.books.ui.screen.FavoritesScreen
import com.tigran.books.ui.viewmodel.BookViewModel

sealed class Screen(val route: String, val label: String) {
    data object Search : Screen("search", "Search")
    data object Favorites : Screen("favorites", "Favorites")
    data object BookDetails : Screen("details", "Details")
}


@Composable
fun BooksNavGraph(
    navController: NavHostController,
    viewModel: BookViewModel,
    modifier: Modifier
) {
    NavHost(modifier = modifier, navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            SearchScreen(viewModel, navController)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(viewModel, navController)
        }
        composable(Screen.BookDetails.route) {
            BookDetailsScreen(viewModel)
        }
    }

}