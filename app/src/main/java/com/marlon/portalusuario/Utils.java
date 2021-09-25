package com.marlon.portalusuario;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import java.lang.reflect.Method;
import java.util.Objects;

public class Utils {
    private Context context;


    public Utils(Context context2) {
        this.context = context2;
    }

    public Utils() {
    }

    public static void CreateAsset(TextView textView, String str) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), str));
    }

    public boolean PermissionUELocation(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.ACCESS_COARSE_LOCATION") == 0 && ActivityCompat.checkSelfPermission(context2, "android.permission.READ_PHONE_STATE") == 0;
    }


    public boolean PermissionUELocationGPS(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.ACCESS_COARSE_LOCATION") == 0 && ActivityCompat.checkSelfPermission(context2, "android.permission.ACCESS_FINE_LOCATION") == 0;
    }

    public boolean openLocation(Context context2) {
        boolean z;
        boolean z2;
        LocationManager locationManager = (LocationManager) context2.getSystemService("location");
        try {
            z = locationManager.isProviderEnabled("gps");
        } catch (Exception e) {
            e.printStackTrace();
            z = false;
        }
        try {
            z2 = locationManager.isProviderEnabled("network");
        } catch (Exception e2) {
            e2.printStackTrace();
            z2 = false;
        }
        if (z || z2) {
            return true;
        }
        return false;
    }
}