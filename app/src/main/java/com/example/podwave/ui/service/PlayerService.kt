package com.example.podwave.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.podwave.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class PlayerService : Service() {

    private lateinit var exoPlayer: ExoPlayer
    private var title: String = ""
    private var imageUrl: String = ""
    private var audioUrl: String = ""
    private var channelId = ""
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build()
        channelId = getString(R.string.channel)
        createNotificationChannel()
    }

    //➡️➡️
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        audioUrl = intent?.getStringExtra("AUDIO_URL") ?: return START_NOT_STICKY
        title = intent.getStringExtra("TITLE") ?: "Tocando"
        imageUrl = intent.getStringExtra("IMAGE_URL")!!

        val mediaItem = MediaItem.fromUri(audioUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        val notification = createNotification(title, imageUrl)
        startForeground(notificationId, notification)

        return START_STICKY
    }

    //💥💥
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //💬💬
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.notification_name) + title,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_description)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    //💬💬
    private fun createNotification(title: String, imageUrl: String?): Notification {
        val bitmap: Bitmap? = try {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            null
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(title)
            .setSmallIcon(R.drawable.podcast_icon)
            .setLargeIcon(bitmap)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}
