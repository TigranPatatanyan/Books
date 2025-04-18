package com.tigran.domain.usecase

import androidx.paging.PagingData
import com.tigran.domain.model.Book
import com.tigran.domain.repository.BookRemoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val repository: BookRemoteRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Book>> {
        return repository.searchBooks(query)
    }
}