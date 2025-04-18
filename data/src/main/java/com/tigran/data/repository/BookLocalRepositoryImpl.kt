package com.tigran.data.repository

import com.tigran.data.db.BookDao
import com.tigran.data.mapper.toDomain
import com.tigran.data.mapper.toEntities
import com.tigran.domain.model.Book
import com.tigran.domain.repository.BookLocalRepository
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File

class BookLocalRepositoryImpl(private val dao: BookDao) : BookLocalRepository {
    override suspend fun getCachedBooks(): List<Book> {
        return dao.getAllBooksWithAuthors().map { it.toDomain() }
    }

    override suspend fun cacheBooks(books: List<Book>, filesDir: String) {
        books.forEachIndexed { index, book ->
            val smallPath = book.smallThumbnailUrl?.let {
                downloadImageAndSave(filesDir, it, "small_${book.id}_$index.jpg")
            }
            val thumbPath = book.thumbnailUrl?.let {
                downloadImageAndSave(filesDir, it, "thumb_${book.id}_$index.jpg")
            }

            val bookWithPaths = book.copy(
                smallThumbnailPath = smallPath,
                thumbnailPath = thumbPath
            )

            val (bookEntity, authors, crossRefs) = bookWithPaths.toEntities()
            dao.insertBook(bookEntity)
            dao.insertAuthors(authors)
            dao.insertCrossRefs(crossRefs)
        }
    }

    private fun downloadImageAndSave(filesDir: String, url: String, fileName: String): String? {
        return try {
            val request = Request.Builder().url(url).build()
            val client = OkHttpClient()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val file = File(filesDir, fileName)
                val sink = file.sink().buffer()
                sink.writeAll(response.body!!.source())
                sink.close()
                file.absolutePath
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}