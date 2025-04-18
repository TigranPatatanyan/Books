package com.tigran.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.tigran.domain.model.Book
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

object BookCachingWorkerHelper {
    fun enqueue(context: Context, books: List<Book>): UUID {
        val booksJson = Json.encodeToString(books)
        val inputData = workDataOf("books" to booksJson)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<BookCachingWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        return workRequest.id
    }
}