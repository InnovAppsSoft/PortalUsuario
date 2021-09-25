package com.marlon.portalusuario.usodedatos.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.IBinder
import android.widget.RemoteViews
import com.marlon.portalusuario.R
import com.marlon.portalusuario.usodedatos.activities.summary.SummaryActivity
import com.marlon.portalusuario.usodedatos.database.DailyConsumption
import com.marlon.portalusuario.usodedatos.util.DateUtils
import com.marlon.portalusuario.usodedatos.util.ImageUtils
import com.marlon.portalusuario.usodedatos.util.TrafficUtils
import com.marlon.portalusuario.usodedatos.viewmodels.ConsumptionViewModel
import java.util.*

@SuppressLint("NewApi")
class TrafficStatusService: Service() {

    private val timer by lazy { Timer() }
    private val NOTIFICATION_ID = 1
    private val CHANNEL_ID = "traffic_service"

    private val consumptionViewModel by lazy {
        ConsumptionViewModel(application)
    }

    private val notificationLayout by lazy {
        RemoteViews("com.marlon.portalusuario",
            R.layout.custom_notification_view)
    }

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val builder by lazy {
        Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(Icon.createWithBitmap(ImageUtils.createBitmapFromString("0", "KB")))
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(false)
            .setAutoCancel(true)
            .setCustomContentView(notificationLayout)
            .setContentIntent(createPendingIntent())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel(CHANNEL_ID, "Traffic Status Service")

        startForeground(NOTIFICATION_ID, builder.build())

        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                val downloadSpeed = TrafficUtils.getNetworkSpeed()
                saveToDB(downloadSpeed)
                updateNotification(downloadSpeed)
            }
        }, 0, 500)
    }

    fun saveToDB(downloadSpeed: String){
        val speed : String = (downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)).toString()
        val units : String  = (downloadSpeed.subSequence(downloadSpeed.indexOf(" ")+1,downloadSpeed.length)).toString()

        var dailyConsumption = DailyConsumption(DateUtils.getDayID(),System.currentTimeMillis(),0,0,0)

        // Insert or ignore
        consumptionViewModel.insert(dailyConsumption)

        val toBytes = TrafficUtils.convertToBytes(speed.toFloat(),units)

        if(TrafficUtils.isWifiConnected(this)){
            consumptionViewModel.updateWifiUsage(dailyConsumption.dayID,toBytes)
        } else {
            consumptionViewModel.updateMobileUsage(dailyConsumption.dayID,toBytes)
        }

    }

    private fun updateNotification(downloadSpeed: String) {
        val speed = downloadSpeed.subSequence(0, downloadSpeed.indexOf(" ")+1)
        val units = downloadSpeed.subSequence(downloadSpeed.indexOf(" ")+1,downloadSpeed.length)

        val bitmap = ImageUtils.createBitmapFromString(speed.toString(), units.toString())
        val icon = Icon.createWithBitmap(bitmap)

        builder.setSmallIcon(icon)


        notificationLayout.setTextViewText(R.id.custom_notification_speed_tv,"$downloadSpeed/s")
        val it = consumptionViewModel.getDayUsageInBackgroundThread(DateUtils.getDayID())

        notificationLayout.setTextViewText(
            R.id.custom_notification_mobile_tv,
            TrafficUtils.getMetricData(it.mobile)
        )
        notificationLayout.setTextViewText(
            R.id.custom_notification_wifi_tv,
            TrafficUtils.getMetricData(it.wifi))

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel("traffic_service", "Traffic Status Service")
        startForeground(NOTIFICATION_ID, builder.build())
        return START_STICKY
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent(this, SummaryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        return PendingIntent.getActivity(this,0,intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_LOW)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(chan)
        return channelId
    }
}