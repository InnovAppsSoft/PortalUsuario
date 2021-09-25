package com.marlon.portalusuario.senal;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class d {

    public static void a(Context context) {
        try {
            Intent intent = new Intent(context, Servicio.class);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception e) {
            Log.w("PU helpers S.A.", e.getMessage());
        }
    }

}