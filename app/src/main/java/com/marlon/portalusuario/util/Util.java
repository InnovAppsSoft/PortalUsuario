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

import com.marlon.portalusuario.logging.JCLogging;

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

    public static void CreateAsset(TextView textView, String str) {
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), str));
    }

    public boolean PermissionUECall(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.CALL_PHONE") == 0;
    }

    public boolean PermissionUECallAnswer(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.ANSWER_PHONE_CALLS") == 0;
    }

    public boolean PermissionUECallAnsw(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.ANSWER_PHONE_CALLS") == 0;
    }

    public boolean PermissionUEAlertW(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.SYSTEM_ALERT_WINDOW") == 0;
    }

    public boolean PermissionUEWakeLock(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.WAKE_LOCK") == 0;
    }

    public boolean PermissionUESMS(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.SEND_SMS") == 0;
    }

    public boolean PermissionUEData(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_SMS") == 0;
    }

    public boolean PermissionUECamera(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.CAMERA") == 0;
    }

    public boolean PermissionUEContact(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_CONTACTS") == 0;
    }

    public boolean PermissionUEWContact(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.WRITE_CONTACTS") == 0;
    }

    public boolean PermissionUEStorageExt(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    public boolean PermissionUEReadCallLogs(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_CALL_LOG") == 0;
    }

    public boolean PermissionUEReadSMS(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_SMS") == 0;
    }

    public boolean PermissionUEStorageExtInit(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_EXTERNAL_STORAGE") == 0;
    }

    public boolean PermissionUEStorageInt(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.WRITE_EXTERNAL_STORAGE") == 0;
    }

    public boolean PermissionUELocation(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.ACCESS_COARSE_LOCATION") == 0 && ActivityCompat.checkSelfPermission(context2, "android.permission.READ_PHONE_STATE") == 0;
    }

    public boolean PermissionUERPhoneState() {
        return ActivityCompat.checkSelfPermission(this.context, "android.permission.READ_PHONE_STATE") == 0;
    }

    public boolean PermissionUERPhoneState(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.READ_PHONE_STATE") == 0;
    }

    public boolean PermissionUELocationGPS(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.ACCESS_COARSE_LOCATION") == 0 && ActivityCompat.checkSelfPermission(context2, "android.permission.ACCESS_FINE_LOCATION") == 0;
    }

    public boolean PermissionUECalendar(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.WRITE_CALENDAR") == 0;
    }

    public boolean PermissionUEBIND_ACCESSIBILITY_SERVICE(Context context2) {
        return ActivityCompat.checkSelfPermission(context2, "android.permission.BIND_ACCESSIBILITY_SERVICE") == 0;
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

    // extract text from RAW Resource
    public static String getTextFrom(Context context, int resource){
        StringBuilder text = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(context.getResources().openRawResource(resource), StandardCharsets.ISO_8859_1);
            BufferedReader br = new BufferedReader(isr);

            String line;

            while((line = br.readLine()) != null)
            {
                text.append(line);
                text.append("\n");
            }
            br.close();
            isr.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        Log.e("TERMINOS", text.toString());
        return text.toString();
    }

    //
    @SuppressLint("SimpleDateFormat")
    public static String date2String(GregorianCalendar date){
        return new SimpleDateFormat("dd/MM/yyyy hh:mm aa").format(date.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static Date string2Date(String date) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy").parse(date);
    }

    public static long date2Long(GregorianCalendar date){
        return date.getTimeInMillis();
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
