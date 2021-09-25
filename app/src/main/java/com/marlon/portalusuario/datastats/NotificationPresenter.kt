package com.marlon.portalusuario.datastats

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.marlon.portalusuario.R
import com.marlon.portalusuario.util.MyLog
import java.lang.ref.WeakReference

internal class NotificationPresenter(service: Service) {

    private val mServiceRef: WeakReference<Service> = WeakReference(service)


    fun showNotification(visibleOverlayView: Boolean) {

        MyLog.d("showNotification")

        val service = mServiceRef.get() ?: return

        val intent = Intent(service, WidgetActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(service, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(service.applicationContext, CHANNEL_ID)


        val notificationLayout = createCustomLayout(service, visibleOverlayView)
        builder.setContentIntent(null)
        builder.setCustomContentView(notificationLayout)


        builder.setSmallIcon(R.drawable.transparent_image)
        builder.setOngoing(true)
        builder.priority = NotificationCompat.PRIORITY_MIN

        builder.setContentTitle(service.getString(R.string.resident_service_running))
//        builder.setContentText("表示・非表示を切り替える");
        builder.setContentIntent(pendingIntent)

        service.startForeground(MY_NOTIFICATION_ID, builder.build())
    }

    private fun createCustomLayout(service: Service, visibleOverlayView: Boolean): RemoteViews {

        val notificationLayout = RemoteViews(service.packageName, R.layout.custom_notification)

        // show button
        if (!visibleOverlayView) {
            val switchIntent = Intent(service, SwitchButtonReceiver::class.java)
            switchIntent.action = "show"
            val switchPendingIntent = PendingIntent.getBroadcast(service, 0, switchIntent, 0)
            notificationLayout.setOnClickPendingIntent(R.id.show_button, switchPendingIntent)
            notificationLayout.setViewVisibility(R.id.show_button, View.VISIBLE)
        } else {
            notificationLayout.setViewVisibility(R.id.show_button, View.GONE)
        }

        // hide button
        if (visibleOverlayView) {
            val switchIntent = Intent(service, SwitchButtonReceiver::class.java)
            switchIntent.action = "hide"
            val switchPendingIntent = PendingIntent.getBroadcast(service, 0, switchIntent, 0)
            notificationLayout.setOnClickPendingIntent(R.id.hide_button, switchPendingIntent)
            notificationLayout.setViewVisibility(R.id.hide_button, View.VISIBLE)
        } else {
            notificationLayout.setViewVisibility(R.id.hide_button, View.GONE)
        }

        // timer (hide and resume) button
        if (visibleOverlayView) {
            val switchIntent = Intent(service, SwitchButtonReceiver::class.java)
            switchIntent.action = "hide_and_resume"
            val switchPendingIntent = PendingIntent.getBroadcast(service, 0, switchIntent, 0)
            notificationLayout.setOnClickPendingIntent(R.id.hide_and_resume_button, switchPendingIntent)
            notificationLayout.setViewVisibility(R.id.hide_and_resume_button, View.VISIBLE)
        } else {
            notificationLayout.setViewVisibility(R.id.hide_and_resume_button, View.INVISIBLE)
        }

        return notificationLayout
    }

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val service = mServiceRef.get() ?: return

            val channel = NotificationChannel(CHANNEL_ID, service.getString(R.string.resident_notification),
                    NotificationManager.IMPORTANCE_LOW)

            // invisible on lockscreen
            channel.lockscreenVisibility = Notification.VISIBILITY_SECRET

            val nm = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun hideNotification() {

        MyLog.d("hideNotification")

        val service = mServiceRef.get() ?: return

        val nm = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(MY_NOTIFICATION_ID)
    }

    companion object {

        private const val MY_NOTIFICATION_ID = 1

        private const val CHANNEL_ID = "resident"
    }

}
