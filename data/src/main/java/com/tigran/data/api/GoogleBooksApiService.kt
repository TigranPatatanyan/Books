package com.tigran.data.api

import com.tigran.data.model.BookResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int
    ): BookResponse
}