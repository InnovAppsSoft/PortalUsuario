package com.marlon.portalusuario.datastats

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.marlon.portalusuario.util.MyLog


class SwitchButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val action = intent?.action
        MyLog.d("SwitchButtonReceiver.onReceive [$action]")

        val serviceIntent = Intent(context, LayerService::class.java)
        serviceIntent.action = action
        context.startService(serviceIntent)
    }
}
