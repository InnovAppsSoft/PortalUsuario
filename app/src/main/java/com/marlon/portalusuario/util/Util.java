package com.marlon.portalusuario.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.marlon.portalusuario.errores_log.JCLogging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class Util {
    private static Context context;
    // LOGGING
    private static JCLogging Logging;

    public Util(Context context2) {
        this.context = context2;
        Logging = new JCLogging(context2);
    }

    public Util() {
    }
    
    // chequear conexion por wifi o datos
    public static boolean isConnected(Context context2) {
        ConnectivityManager cm;
        NetworkInfo activeNetworks;
        cm = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetworks = cm.getActiveNetworkInfo();
        if (activeNetworks != null) {
            try {
                return (isConnectedByMobileData(context2) || isConnectedByWifi(context2));
                /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */
            }catch (Exception ex){
                ex.printStackTrace();
                Logging.error(null, null, ex);
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static boolean isConnectedByWifi(Context ctx){
        try{
            ConnectivityManager connManager1 = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
        return false;
    }

    public static boolean isConnectedByMobileData(Context ctx){
        try{
            ConnectivityManager connManager1 = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mData = connManager1.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mData.isConnected()) {
                return true;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
        return false;
    }

//    // chequear conexion por datos moviles
//    public static boolean isDataEnabled(Context context2) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
//        try {
//            Objects.requireNonNull(connectivityManager);
//            Method declaredMethod = Class.forName(connectivityManager.getClass().getName()).getDeclaredMethod("getMobileDataEnabled");
//            declaredMethod.setAccessible(true);
//            return (Boolean) declaredMethod.invoke(connectivityManager, new Object[0]);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

//    public static boolean isConnectedByData(Context context2) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
//        try {
//            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//            if (capabilities != null) {
//                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_CELLULAR");
//                    return true;
//                } catch(Exception e){
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//        }
//    }

//    // chequear conexion por wifi
//    public static boolean isWifiEnabled(Context context2) {
//        try {
//            WifiManager wifiManager = (WifiManager) context2.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            Objects.requireNonNull(wifiManager);
//            return wifiManager.isWifiEnabled();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }


//    public static boolean isConnectedByWifi(Context context2) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService(Context.CONNECTIVITY_SERVICE);
//        try {
//            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
//            if (capabilities != null) {
//                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//                    Log.i(TAG, "NetworkCapabilities.TRANSPORT_WIFI");
//                    return true;
//                } catch(Exception e){
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//        }
//    }

    @SuppressLint("SimpleDateFormat")
    public static String date2String(GregorianCalendar date){
        return new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(date.getTime());
    }

    public static GregorianCalendar long2Date(long timestamp){
        GregorianCalendar date = new GregorianCalendar();
        date.setTimeInMillis(timestamp);
        return date;
    }

    public static long currentDate2Long(){
        return currentDate().getTimeInMillis();
    }

    public static GregorianCalendar currentDate(){
        GregorianCalendar date = new GregorianCalendar();
        return date;
    }

    public static double roundDouble(double numero) {
        try{
            return Double.parseDouble(new DecimalFormat("###.##").format(numero));
        }catch (Exception ex){
            ex.printStackTrace();
            Logging.error(null, null, ex);
        }
        return new Double(0);
    }
}
