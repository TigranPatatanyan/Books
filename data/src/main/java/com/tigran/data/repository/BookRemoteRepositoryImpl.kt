package com.tigran.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tigran.data.api.GoogleBooksApiService
import com.tigran.data.api.GoogleBooksPagingSource
import com.tigran.domain.model.Book
import com.tigran.domain.repository.BookRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookRemoteRepositoryImpl @Inject constructor(
    private val api: GoogleBooksApiService
) : BookRemoteRepository {

    override fun searchBooks(query: String): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GoogleBooksPagingSource(api, query) }
        ).flow
    }
}