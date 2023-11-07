package com.example.anew

import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val title = inputData.getString("title")
        val content = inputData.getString("content")
        val channelId = inputData.getString("channelId")
        val soundUriString = inputData.getString("soundUri")

        val builder = NotificationCompat.Builder(applicationContext, channelId.toString())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (soundUriString != null) {
            val soundUri = Uri.parse(soundUriString)
            builder.setSound(soundUri)
        }
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build())
        }
        return Result.success()
    }
}
