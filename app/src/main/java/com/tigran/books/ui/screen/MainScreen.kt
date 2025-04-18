package com.tigran.books.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tigran.books.navigation.BooksNavGraph
import com.tigran.books.navigation.Screen
import com.tigran.books.ui.viewmodel.BookViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val viewModel: BookViewModel = hiltViewModel()

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.BookDetails.route)
                NavigationBar {
                    Spacer(Modifier.width(32.dp))
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text(Screen.Search.label) },
                        selected = currentRoute == Screen.Search.route,
                        onClick = {
                            if (currentRoute != Screen.Search.route) {
                                navController.navigate(Screen.Search.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                        label = { Text(Screen.Favorites.label) },
                        selected = currentRoute == Screen.Favorites.route,
                        onClick = {
                            if (currentRoute != Screen.Favorites.route) {
                                navController.navigate(Screen.Favorites.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                    Spacer(Modifier.width(32.dp))
                }
        }
    ) { padding ->
        Box {
            BooksNavGraph(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.padding(padding)
            )
            ErrorHandlerComponent(viewModel)
        }
    }
}
