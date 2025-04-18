package com.tigran.data.api

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tigran.data.mapper.toDomain
import com.tigran.domain.model.Book

class GoogleBooksPagingSource(
    private val api: GoogleBooksApiService,
    private val query: String
) : PagingSource<Int, Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 0
        val pageSize = params.loadSize

        return try {
            val response = api.searchBooks(query, startIndex = page, maxResults = pageSize)
            val books = response.items?.mapNotNull { it.toDomain() }.orEmpty()

            return LoadResult.Page(
                data = books,
                prevKey = if (page == 0) null else page - pageSize,
                nextKey = if (books.isEmpty()) null else page + pageSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition
    }
}