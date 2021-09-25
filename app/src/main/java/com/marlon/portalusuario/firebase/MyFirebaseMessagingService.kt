package com.marlon.portalusuario.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.marlon.portalusuario.Main3Activity
import com.marlon.portalusuario.R

/**
 *
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {


    private val TAG = "FireBaseMessagingService"
    var NOTIFICATION_CHANNEL_ID = "com.marlon.notification"
    val NOTIFICATION_ID = 100

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.e("message", "Message Received ...");

        if (p0.data.isNotEmpty()) {
            val title = p0.data["title"]
            val body = p0.data["body"]
            showNotification(applicationContext, title, body)
        } else {
            val title = p0.notification!!.title
            val body = p0.notification!!.body
            showNotification(applicationContext, title, body)
        }
    }

    override fun onNewToken(p0: String){
        super.onNewToken(p0)
        Log.e("token", "New Token")
    }


    fun showNotification(
            context: Context,
            title: String?,
            message: String?
    ) {
        val ii: Intent
        ii = Intent(context, Main3Activity::class.java)
        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()
        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi =
                PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Log.e("Notification", "Created in up to orio OS device");
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(true)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    title,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.ic_event else R.drawable.ic_event
    }

}