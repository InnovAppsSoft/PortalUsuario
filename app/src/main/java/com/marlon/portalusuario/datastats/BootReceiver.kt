package com.marlon.portalusuario.datastats

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager
import com.marlon.portalusuario.util.MyLog


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        MyLog.i("BootReceiver.onReceive")

        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {


            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            val startOnBoot = pref.getBoolean(C.PREF_KEY_START_ON_BOOT, true)

            MyLog.i("start on boot[" + (if (startOnBoot) "YES" else "NO") + "]")

            if (startOnBoot) {

                val serviceIntent = Intent(context, LayerService::class.java)
                if (Build.VERSION.SDK_INT >= 26) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }

    }
}
