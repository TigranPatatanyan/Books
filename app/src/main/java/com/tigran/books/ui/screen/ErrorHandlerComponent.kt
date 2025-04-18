package com.tigran.books.ui.screen

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.tigran.books.ui.viewmodel.BookIntent
import com.tigran.books.ui.viewmodel.BookViewModel
import com.tigran.books.ui.viewmodel.LoadingState

@Composable
fun ErrorHandlerComponent(viewModel: BookViewModel) {
    val state = viewModel.uiState.collectAsState()
    val loadingState = state.value.loadingState
    val context = LocalContext.current.applicationContext
    (loadingState as? LoadingState.RemoteBooksLoadingState.NoNetwork)?.let {
        Toast.makeText(context, "Oops! Network Error", Toast.LENGTH_SHORT).show()
        viewModel.handleIntent(BookIntent.ResetLoadingState)
    }
    (loadingState as? LoadingState.WorkerDownloadState.Failed)?.let {
        AlertDialog(
            onDismissRequest = {
                viewModel.handleIntent(BookIntent.ResetLoadingState)
            },
            title = { Text("Oops!") },
            text = { Text("Failed To Download Favorite Books") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.handleIntent(
                        BookIntent.DownloadAllFavorites(
                            context
                        )
                    )
                }) {
                    Text("Retry")
                }

            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.handleIntent(BookIntent.ResetLoadingState)
                }) {
                    Text("Close")
                }
            }
        )
    }
    (loadingState as? LoadingState.LocalFavoriteBooksLoadingState.NotFound)?.let {
        Toast.makeText(context, "You Have Not Saved Favorite Books", Toast.LENGTH_SHORT).show()
        viewModel.handleIntent(BookIntent.ResetLoadingState)
    }
}