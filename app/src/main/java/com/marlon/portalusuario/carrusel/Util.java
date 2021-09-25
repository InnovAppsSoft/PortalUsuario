package com.marlon.portalusuario.carrusel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import java.lang.reflect.Method;
import java.util.Objects;

public class Util {
    private Context context;

    public Util(Context context2) {
        this.context = context2;
    }

    public Util() {
    }

    public static boolean isConnected(Context context2) {
        boolean enabled_wifi = false;
        boolean enabled_data = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Objects.requireNonNull(connectivityManager);
            Method declaredMethod = Class.forName(connectivityManager.getClass().getName()).getDeclaredMethod("getMobileDataEnabled", new Class[0]);
            declaredMethod.setAccessible(true);
            enabled_data = (Boolean) declaredMethod.invoke(connectivityManager, new Object[0]);
            try {
                WifiManager wifiManager = (WifiManager) context2.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                Objects.requireNonNull(wifiManager);
                enabled_wifi = wifiManager.isWifiEnabled();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        //enabled_data || enabled_wifi;
        return enabled_data && !enabled_wifi;
    }

    // chequear conexion por datos moviles
    public static boolean isConnectedByData(Context context2) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Objects.requireNonNull(connectivityManager);
            Method declaredMethod = Class.forName(connectivityManager.getClass().getName()).getDeclaredMethod("getMobileDataEnabled");
            declaredMethod.setAccessible(true);
            return (Boolean) declaredMethod.invoke(connectivityManager, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // chequear conexion por wifi
    public static boolean isConnectedByWifi(Context context2) {
        try {
            WifiManager wifiManager = (WifiManager) context2.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            Objects.requireNonNull(wifiManager);
            return wifiManager.isWifiEnabled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // reajustar tamanno de Bitmap
    public static Drawable resize(Context context2, Drawable drawable) {
        return new BitmapDrawable(context2.getResources(), Bitmap.createScaledBitmap(((BitmapDrawable) drawable).getBitmap(), 50, 50, false));
    }

    // chequear Localizacion
    public boolean openLocation(Context context2) {
        boolean gps = false;
        boolean network = false;
        LocationManager locationManager = (LocationManager) context2.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps = locationManager.isProviderEnabled("gps");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network = locationManager.isProviderEnabled("network");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return gps || network;
    }
}
