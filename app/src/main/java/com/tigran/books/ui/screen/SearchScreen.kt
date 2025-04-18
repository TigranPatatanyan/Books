package com.tigran.books.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.tigran.books.navigation.Screen
import com.tigran.books.ui.viewmodel.BookIntent
import com.tigran.books.ui.viewmodel.BookViewModel
import com.tigran.domain.model.Book

@Composable
fun SearchScreen(
    viewModel: BookViewModel, navController: NavController
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()
    val books = viewModel.books.collectAsLazyPagingItems()
    val loadedBooks = remember(books.itemCount) {
        (0 until books.itemCount).mapNotNull { index -> books[index] }
    }

    //couldn't find another Working method for catching paging error
    LaunchedEffect(books.loadState) {
        if (books.loadState.hasError){
            viewModel.handleIntent(BookIntent.NotifyNetworkError)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.handleIntent(BookIntent.Search(it)) },
            label = { Text("Search books") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }),
            modifier = Modifier
                .fillMaxWidth()
        )
        LazyColumn {
            items(loadedBooks) { book ->
                BookItem(book, onItemClick = {
                    viewModel.handleIntent(BookIntent.BookDetails(book))
                    navController.navigate(Screen.BookDetails.route)
                }, onAddToFavoritesClick = {
                    viewModel.handleIntent(
                        BookIntent.AddToFavorites(it)
                    )
                })
            }

            when (books.loadState.append) {
                is LoadState.Loading -> item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }

                else -> {}
            }
        }
    }
}


@Composable
fun BookItem(
    book: Book, onItemClick: (Book) -> Unit, onAddToFavoritesClick: ((Book) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onItemClick(book) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    book.smallThumbnailPath ?: book.smallThumbnailUrl
                ),
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.authors.firstOrNull() ?: "Unknown Author",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            onAddToFavoritesClick?.let {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Add to favorites",
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { it(book) }
                        .padding(8.dp))
            }
        }
    }
}
