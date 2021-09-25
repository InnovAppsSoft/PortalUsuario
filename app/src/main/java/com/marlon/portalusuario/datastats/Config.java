package com.marlon.portalusuario.datastats;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.marlon.portalusuario.util.MyLog;
import com.marlon.portalusuario.util.TkConfig;


public class Config {

    public static long highLimit;
    public static long middleLimit;

    public static int xPos = 90;  // [0, 100]
    public static int barMaxKB = 100;
    public static boolean unitTypeBps;

    public static boolean logBar = true;
    public static int intervalMs = 1000;
    public static boolean hideWhenInFullscreen = true;
    
    public static boolean interpolateMode = false;
    
    public static int textSizeSp = C.DEFAULT_TEXT_SIZE_SP;


    public static void loadPreferences(Context context) {

        MyLog.d("Config.loadPreferences");

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        TkConfig.debugMode = pref.getBoolean(C.PREF_KEY_DEBUG_MODE, false);
        xPos = pref.getInt(C.PREF_KEY_X_POS, 100);
        intervalMs = pref.getInt(C.PREF_KEY_INTERVAL_MSEC, 1000);
        barMaxKB = pref.getInt(C.PREF_KEY_BAR_MAX_SPEED_KB, 10240);
        unitTypeBps = pref.getBoolean(C.PREF_KEY_UNIT_TYPE_BPS, false);
        logBar = pref.getBoolean(C.PREF_KEY_LOGARITHM_BAR, true);
        hideWhenInFullscreen = pref.getBoolean(C.PREF_KEY_HIDE_WHEN_IN_FULLSCREEN, true);
        interpolateMode = pref.getBoolean(C.PREF_KEY_INTERPOLATE_MODE, false);
        textSizeSp = pref.getInt(C.PREF_KEY_TEXT_SIZE_SP, C.DEFAULT_TEXT_SIZE_SP);


        if (logBar) {

            final double pMiddleLimit = 0.3;
            middleLimit = (long) (barMaxKB / 100.0 * Math.pow(10.0, pMiddleLimit * 5.0));


            final double pHighLimit = 0.6;
            highLimit = (long) (barMaxKB / 100.0 * Math.pow(10.0, pHighLimit * 5.0));
        } else {
            middleLimit = 10 * 1024;
            highLimit = 100 * 1024;
        }
        MyLog.d("loadPreferences: update limit for colors: middle[" + middleLimit + "B], high[" + highLimit + "B]");
    }
}
