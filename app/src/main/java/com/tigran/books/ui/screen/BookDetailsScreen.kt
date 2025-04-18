package com.tigran.books.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.tigran.books.ui.viewmodel.BookViewModel

@Composable
fun BookDetailsScreen(viewModel: BookViewModel) {
    Column(Modifier.padding(16.dp)) {
        val state = viewModel.uiState.collectAsState()
        val book = state.value.book ?: throw Exception()
        Image(
            painter = rememberAsyncImagePainter(
                book.thumbnailPath
                    ?: book.thumbnailUrl
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Text(book.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        book.authors.forEach{
            Text(it, fontWeight = FontWeight.Medium, fontSize = 16.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}
