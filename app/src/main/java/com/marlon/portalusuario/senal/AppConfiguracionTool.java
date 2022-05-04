package com.marlon.portalusuario.senal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppConfiguracionTool {
    public static final int CONFIG_GENERAL_FAB_ENVIARSALDO = 0;
    public static final int CONFIG_GENERAL_FAB_LLAMADAS99 = 1;
    public static final int CONFIG_GENERAL_FAB_NAUTA = 4;
    public static final int CONFIG_GENERAL_FAB_PORTAL = 5;
    public static final int CONFIG_GENERAL_FAB_RADIOBASESCERCANAS = 3;
    public static final int CONFIG_GENERAL_FAB_RADIOBASESUSADAS = 2;
    public static final int CONFIG_LLAMADAS_99_ASTEMAS99 = 1;
    public static final int CONFIG_LLAMADAS_99_NORMAL = 0;
    public static final String Config_IsCubacelShow = "config_is_cubacel_show";
    public static final String Config_IsPrimeraEjecucion = "config_is_primera_ejecucion_version200";
    public static final String Config_WidgetOnBlanco = "config_widget_blanco_on";
    public static final String Config_WidgetOnNegro = "config_widget_negro_on";
    public static final String Config_main_veces_open = "config_main_veces_open";
    public static final String Config_noDatabase_File = "config_main";
    public static final String Config_service_reloj_posicion_x = "config_service_reloj_posicion_x";
    public static final String Config_service_reloj_posicion_y = "config_service_reloj_posicion_y";
    public static final String Nauta_Online_ConnectionTimeProgramationStartInMillis = "nauta_online_connectiontimeprogramationstartinmillis";
    public static final String Nauta_Online_ConnectionTimeProgramationTimeInMillis = "nauta_online_connectiontimeprogramationtimeinmillis";
    public static final String Red_CoberturaAlarmRepeat = "red_cobertura_alarm_repeat";

    public static void donateAddOneOpen(Context context) {
        int donateMainOpen = getDonateMainOpen(context);
        if (donateMainOpen <= 150 && donateMainOpen != -1) {
            SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
            edit.putInt(Config_main_veces_open, donateMainOpen + 1);
            edit.apply();
        }
    }

    public static void donateMainOpenDono(Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(Config_main_veces_open, -1);
        edit.apply();
    }

    public static int getDonateMainOpen(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(Config_main_veces_open, 0);
    }

    public static int getFab(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("config_pref_general_fab", "0"));
    }

    public static boolean getIsCubacelShow(Context context) {
        try {
            return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Config_IsCubacelShow, false);
        }catch (Exception ex){
            ex.printStackTrace();
            return true;
        }
    }

    public static boolean getIsPrimeraEjecucion(Context context) {
        try {
            return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Config_IsPrimeraEjecucion, false);
        }catch (Exception ex){
            ex.printStackTrace();
            return true;
        }
    }


    public static void setCoberturaAlarmRepeat(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(Red_CoberturaAlarmRepeat, i);
        edit.apply();
    }

    public static void setIsCubacelShow(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Config_IsCubacelShow, z);
        edit.apply();
    }

    public static void setIsPrimeraEjecucion(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Config_IsPrimeraEjecucion, z);
        edit.apply();
    }

}
