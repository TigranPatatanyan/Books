package com.tigran.domain.usecase

import com.tigran.domain.model.Book
import com.tigran.domain.repository.BookLocalRepository

class LocalUseCase(private val repository: BookLocalRepository) {
    suspend fun getCachedData(): List<Book> {
        return repository.getCachedBooks()
    }
}