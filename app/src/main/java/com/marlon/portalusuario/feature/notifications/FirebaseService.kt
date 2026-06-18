package com.marlon.portalusuario.feature.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.marlon.portalusuario.R
import com.marlon.portalusuario.feature.main.MainActivity
import com.marlon.portalusuario.data.preferences.showNotificationsFlow
import com.marlon.portalusuario.data.preferences.storageAdsFlow
import com.marlon.portalusuario.domain.data.PunRepository
import com.marlon.portalusuario.feature.notifications.PUNotification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.util.GregorianCalendar
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseService : FirebaseMessagingService() {
    @Inject
    lateinit var punRepository: PunRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    @Suppress("DEPRECATION")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let { notification ->
            try {
                Log.e(TAG, "Message Notification Title: ${notification.title}")
                Log.e(TAG, "Message Notification Image Icon: ${notification.imageUrl}")
                Log.e(TAG, "Message Notification Body: ${notification.body}")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            val storageADS = runBlocking { storageAdsFlow().first() }
            Log.e("STORAGE ADS", storageADS.toString())

            val title = notification.title ?: ""
            val body = notification.body ?: ""
            val imageUrl = notification.imageUrl?.toString() ?: ""

            val pun = PUNotification(title, body, imageUrl, GregorianCalendar().timeInMillis)

            if (storageADS) {
                val image = pun.image
                if (image.isNotEmpty()) {
                    try {
                        Glide
                            .with(applicationContext)
                            .asBitmap()
                            .load(image)
                            .into(
                                object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?,
                                    ) {
                                        val start = image.lastIndexOf("/")
                                        val filename = image.substring(start + 1)
                                        val filepath =
                                            applicationContext
                                                .getExternalFilesDir(
                                                    Environment.getDataDirectory().absolutePath,
                                                )?.absolutePath + "/pun/$filename"
                                        val file = File(filepath)
                                        Log.e("IMAGE FILEPATH", filepath)
                                        try {
                                            if (!file.exists()) {
                                                file.createNewFile()
                                            }
                                            FileOutputStream(file).use { os ->
                                                resource.compress(Bitmap.CompressFormat.JPEG, 100, os)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        pun.image = filepath
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}

                                    override fun onLoadFailed(errorDrawable: Drawable?) {
                                        super.onLoadFailed(errorDrawable)
                                    }
                                },
                            )
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                runBlocking { punRepository.insertPUNotification(pun) }
            }
            sendNotification(pun)
        }
    }

    @Suppress("DEPRECATION")
    private fun sendNotification(pun: PUNotification) {
        if (!runBlocking { showNotificationsFlow().first() }) return

        val resultIntent =
            Intent(this, MainActivity::class.java).apply {
                putExtra("open_notifications", true)
            }
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "Mensajes de Portal Usuario",
                    NotificationManager.IMPORTANCE_DEFAULT,
                )
            notificationManager.createNotificationChannel(channel)
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(pun.title)
                .setContentText(pun.title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setSound(defaultSoundUri)
                .setLights(Color.BLUE, 500, 500)
                .setVibrate(longArrayOf(500, 500, 500))
                .setContentIntent(resultPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManagerCompat.notify(0, builder.build())
    }

    private fun getNotificationIcon(): Int = R.drawable.ic_event

    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "PUNoticias"
    }
}
