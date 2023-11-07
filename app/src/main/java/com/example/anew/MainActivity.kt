package com.example.anew

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.anew.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val requestCode = 101

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setNotificationButton.setOnClickListener {
            selectCustomNotificationSound()
        }
    }
    private fun selectCustomNotificationSound() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, requestCode)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode && resultCode == RESULT_OK && data != null) {
            val soundUri = data.data
            if (soundUri != null) {
                val uniqueChannelId = "notification_${System.currentTimeMillis()}"
                createNotificationChannel(uniqueChannelId, soundUri)
                val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                sharedPrefs.edit {
                    putString(uniqueChannelId, soundUri.toString())
                }
                scheduleNotificationWithCustomSound(uniqueChannelId)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String,soundUri: Uri) {
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val name = "Reminder"
        val description = "It's Time To Drink Water"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
        channel.setSound(soundUri,audioAttributes)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.deleteNotificationChannel(channelId)
        notificationManager.createNotificationChannel(channel)
    }
    private fun scheduleNotificationWithCustomSound(channelId: String) {
        val data = Data.Builder()
            .putString("title", "Custom Notification")
            .putString("content", "This is a custom notification")
            .putString("soundUri", channelId)
            .putString("channelId",channelId)
            .build()

        val notificationWork = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(notificationWork)
    }
}
