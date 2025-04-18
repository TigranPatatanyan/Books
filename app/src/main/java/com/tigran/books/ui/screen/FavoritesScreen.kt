package com.tigran.books.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tigran.books.navigation.Screen
import com.tigran.books.ui.viewmodel.BookIntent
import com.tigran.books.ui.viewmodel.BookViewModel
import com.tigran.books.ui.viewmodel.LoadingState

@Composable
fun FavoritesScreen(
    viewModel: BookViewModel, navController: NavController
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.handleIntent(BookIntent.LoadFromLocal)
    }
    val state = viewModel.uiState.collectAsState()
    val favorites = state.value.favorites

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        favorites.isNotEmpty().let {
            val isDownloading = state.value.loadingState == LoadingState.WorkerDownloadState.Downloading
            Text(
                text = if (isDownloading) "Downloading..." else "Download All",
                color = if (isDownloading) Color.Gray else Color.Blue,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .then(
                        if (!isDownloading)
                            Modifier.clickable {
                                viewModel.handleIntent(BookIntent.DownloadAllFavorites(context.applicationContext,))
                            }
                        else Modifier
                    ),
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
        LazyColumn {
            items(favorites.toList()) { book ->
                BookItem(book, onItemClick = {
                    viewModel.handleIntent(BookIntent.BookDetails(book))
                    navController.navigate(Screen.BookDetails.route)
                })
            }
        }
    }
}

