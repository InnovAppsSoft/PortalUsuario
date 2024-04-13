package com.marlon.portalusuario.trafficbubble

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.preference.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        Log.e("FloatingBubble", "starting service...")
        if (FloatingBubbleService.isStarted) {
            return
        }
        val settings = PreferenceManager.getDefaultSharedPreferences(context)

        if (settings.getBoolean("show_traffic_speed_bubble", false)) {
            if (Settings.canDrawOverlays(context)) {
                Log.i("CALLING ON MA", "STARTING SERVICE")
                context.stopService(Intent(context, FloatingBubbleService::class.java))
                context.startService(Intent(context, FloatingBubbleService::class.java))
            }
        }
    }
}
