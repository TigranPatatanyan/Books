package com.tigran.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.tigran.domain.model.Book
import com.tigran.domain.repository.BookLocalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class BookCachingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: BookLocalRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val booksJson = inputData.getString("books") ?: return Result.failure()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            val books = Json.decodeFromString<List<Book>>(booksJson)
            repository.cacheBooks(books, applicationContext.filesDir.path)
            return Result.success()
        }
        setForeground(createForegroundInfo())
        return try {
            val books = Json.decodeFromString<List<Book>>(booksJson)
            repository.cacheBooks(books, applicationContext.filesDir.path)
            NotificationManagerCompat.from(applicationContext).cancel(1001)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val channelId = "book_download_channel"
        val notificationId = 1001
        val channel = NotificationChannel(
            channelId,
            "Book Download",
            NotificationManager.IMPORTANCE_LOW
        )
        NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Downloading books")
            .setContentText("Please wait while books are being cached.")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .build()
        return ForegroundInfo(notificationId, notification)
    }
}
