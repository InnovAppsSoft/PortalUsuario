package com.marlon.portalusuario;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.Objects;

public class Connectivity {
    static final boolean assertionsDisabled = false;

    private static boolean isConnectionFast(int i, int i2) {
        if (i == 1) {
            return true;
        }
        if (i != 0) {
            return false;
        }
        switch (i2) {
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 13:
            case 14:
            case 15:
                return true;
            case 4:
            case 7:
            case 11:
            default:
                return false;
        }
    }

    public String getNetworkTypeConverted(int i) {
        switch (i) {
            case 0:
                return "0G(Unknown)";
            case 1:
                return "GPRS";
            case 2:
                return "EDGE";
            case 3:
                return "UMTS";
            case 4:
                return "CDMA";
            case 5:
                return "EVDO revision 0";
            case 6:
                return "EVDO revision A";
            case 7:
                return "1xRTT";
            case 8:
                return "HSDPA";
            case 9:
                return "HSUPA";
            case 10:
                return "HSPA";
            case 11:
                return "iDen";
            case 12:
                return "EVDO revision B";
            case 13:
                return "4G(LTE)";
            case 14:
                return "eHRPD)";
            case 15:
                return "HSPA+";
            default:
                return "Unknown";
        }
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) Objects.requireNonNull((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public boolean isConnectedWifi(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        if (networkInfo == null || !networkInfo.isConnected() || networkInfo.getType() != 1) {
            return false;
        }
        return true;
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == 0;
    }

    public static boolean isConnectedFast(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected() && isConnectionFast(networkInfo.getType(), networkInfo.getSubtype());
    }


    @SuppressLint("SwitchIntDef")
    public String getNetworkClass(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        switch (((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType()) {
            case 0:
                return "0G - Unknown";
            case 1:
                return "2G - GPRS";
            case 2:
                return "2G - EDGE";
            case 3:
                return "3G - UMTS";
            case 4:
                return "2G - CDMA";
            case 5:
                return "3G - EVDO revision 0";
            case 6:
                return "3G - EVDO revision A";
            case 7:
                return "2G - 1xRTT";
            case 8:
                return "3G - HSDPA";
            case 9:
                return "3G - HSUPA";
            case 10:
                return "3G - HSPA";
            case 11:
                return "2G - iDen";
            case 12:
                return "3G - EVDO revision B";
            case 13:
                return "4G - LTE";
            case 14:
                return "3G - eHRPD";
            case 15:
                return "3G - HSPA+";
            default:
                return "Unknown·";
        }
    }
}
