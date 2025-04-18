package com.tigran.domain.repository

import com.tigran.domain.model.Book

interface BookLocalRepository {
    suspend fun getCachedBooks(): List<Book>
    suspend fun cacheBooks(books: List<Book>, filesDir: String)
}
