package com.marlon.portalusuario.floating_window;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent arg1)
    {
        Log.e("FloatingBubble","starting service...");
        if (FloatingBubbleService.isStarted) {
            return;
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.getBoolean("show_traffic_speed_bubble", false)) {
            if (Settings.canDrawOverlays(context)) {
                Log.i("CALLING ON MA", "STARTING SERVICE");
                context.stopService(new Intent(context, FloatingBubbleService.class));
                context.startService(new Intent(context, FloatingBubbleService.class));
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startFloatingBubbleService(Context context, SharedPreferences settings) {

    }
}