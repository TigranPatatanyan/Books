package com.tigran.data.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
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
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: BookLocalRepository
) : CoroutineWorker(context, workerParams) {

    private val channelId = "book_download_channel"
    private val notificationId = 1001

    override suspend fun doWork(): Result {
        val booksJson = inputData.getString("books") ?: return Result.failure()

        val books = try {
            Json.decodeFromString<List<Book>>(booksJson)
        } catch (e: Exception) {
            Log.e("BookCachingWorker", "Failed to parse books JSON", e)
            return Result.failure()
        }

        val hasNotificationPermission =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

        if (hasNotificationPermission) {
            createNotificationChannel()
        }

        return try {
            repository.cacheBooks(books, context.filesDir.path) { current, total ->
                Log.d("BookCachingWorker", "Caching book $current of $total")
                if (hasNotificationPermission) {
                    val notification = buildNotification(current, total)
                    NotificationManagerCompat.from(context).notify(notificationId, notification)
                }
            }

            if (hasNotificationPermission) {
                NotificationManagerCompat.from(context).cancel(notificationId)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("BookCachingWorker", "Error during book caching", e)
            Result.failure()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        //This will be called by the system when needed
        return createForegroundInfo(0, 0)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Book Download",
                NotificationManager.IMPORTANCE_LOW
            )
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }

    private fun createForegroundInfo(current: Int, total: Int): ForegroundInfo {
        val notification = buildNotification(current, total)
        return ForegroundInfo(notificationId, notification)
    }

    private fun buildNotification(current: Int, total: Int): android.app.Notification {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Caching books")
            .setContentText("Caching $current of $total")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(total, current, false)
            .setOngoing(true)
            .build()
    }
}