package com.tigran.domain.repository

import androidx.paging.PagingData
import com.tigran.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRemoteRepository {
    fun searchBooks(query: String): Flow<PagingData<Book>>
}