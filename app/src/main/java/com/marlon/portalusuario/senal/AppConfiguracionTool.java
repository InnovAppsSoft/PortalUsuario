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
    public static final String Red_CoberturaAlarmTime = "red_cobertura_alarm_tiempo";
    public static final String Red_IsCoberturaAlertShow = "red_cobertura_show";
    public static final String Red_IsCoberturaSonar = "red_cobertura_sonar";
    public static final String Red_IsCoberturaVibrar = "red_cobertura_vibrar";
    public static final String SALDO_ULTIMOENVIO = "config_saldo_ultimoenvio";
    public static final String main_Concurso = "main_conc20182";

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

    public static int get99(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("config_pref_llamadas_99", "0"));
    }

    public static int getCoberturaAlarmRepeat(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(Red_CoberturaAlarmRepeat, 1);
    }

    public static int getCoberturaAlarmTime(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(Red_CoberturaAlarmTime, 1);
    }

    public static int getConcurso(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(main_Concurso, 1);
    }

    public static long getConnectiontimeProgramationStartInMillis(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getLong(Nauta_Online_ConnectionTimeProgramationStartInMillis, 0);
    }

    public static long getConnectiontimeProgramationTimeInMillis(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getLong(Nauta_Online_ConnectionTimeProgramationTimeInMillis, 0);
    }

    public static int getDonateMainOpen(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(Config_main_veces_open, 0);
    }

    public static int getFab(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("config_pref_general_fab", "0"));
    }

    public static String getIdioma(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("config_pref_general_idioma", "es");
    }

    public static boolean getIsCoberturaAlertShow(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Red_IsCoberturaAlertShow, false);
    }

    public static boolean getIsCoberturaSonar(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Red_IsCoberturaSonar, true);
    }

    public static boolean getIsCoberturaVibrar(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Red_IsCoberturaVibrar, true);
    }

    public static boolean getIsCubacelShow(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Config_IsCubacelShow, false);
    }

    public static boolean getIsPrimeraEjecucion(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Config_IsPrimeraEjecucion, false);
    }

    public static int getRelojPosicionX(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(Config_service_reloj_posicion_x, 0);
    }

    public static int getRelojPosicionY(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getInt(Config_service_reloj_posicion_y, 10);
    }

    public static boolean getShowBurbuja(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_wifi_burbuja", true);
    }

    public static boolean getShowBurbujaInDATA(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_show_burbuja_data", true);
    }

    public static boolean getShowBurbujaInWIFI(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_show_burbuja_wifi", true);
    }

    public static boolean getShowBurbujaTraffic(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_wifi_burbujatraffic", true);
    }

    public static boolean getShowCaptivePortal(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_wifi_captive", true);
    }

    public static boolean getShowFab(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_general_fab_view", true);
    }

    public static boolean getShowSlideMenu(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_general_slide_view", true);
    }

    public static int getSlideMenuPosition(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("config_pref_general_slide_position", "0"));
    }

    public static int getTheme(Context context) {
        int i = PreferenceManager.getDefaultSharedPreferences(context).getInt("config_pref_general_theme", 2131755210);
        switch (i) {
            case -14575885:
                return 2131755210;
            case -14208456:
                return 2131755224;
            case -12627531:
                return 2131755228;
            case -11751600:
                return 2131755226;
            case -10011977:
                return 2131755232;
            case -6381922:
                return 2131755227;
            case -1499549:
                return 2131755231;
            case -769226:
                return 2131755233;
            case -26624:
                return 2131755230;
            case -16121:
                return 2131755208;
            default:
                return i;
        }
    }

    public static int getThemeConfig(Context context) {
        int i = PreferenceManager.getDefaultSharedPreferences(context).getInt("config_pref_general_theme", 2131755215);
        switch (i) {
            case -14575885:
                return 2131755215;
            case -14208456:
                return 2131755216;
            case -12627531:
                return 2131755219;
            case -11751600:
                return 2131755217;
            case -10011977:
                return 2131755222;
            case -6381922:
                return 2131755218;
            case -1499549:
                return 2131755221;
            case -769226:
                return 2131755223;
            case -26624:
                return 2131755220;
            case -16121:
                return 2131755214;
            default:
                return i;
        }
    }

    public static long getUltimoEnvio(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getLong(SALDO_ULTIMOENVIO, 0);
    }

    public static boolean getWidgetOnBlanco(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Config_WidgetOnBlanco, false);
    }

    public static boolean getWidgetOnNegro(Context context) {
        return context.getSharedPreferences(Config_noDatabase_File, 0).getBoolean(Config_WidgetOnNegro, false);
    }

    public static boolean getWifiNoLoginInsecure(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_wifi_nologin_insecure", false);
    }

    public static boolean getWifiOffAfterDisconected(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("config_pref_wifi_turnoff_afterdisconnect", true);
    }

    public static void setCoberturaAlarmRepeat(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(Red_CoberturaAlarmRepeat, i);
        edit.apply();
    }

    public static void setCoberturaAlarmTime(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(Red_CoberturaAlarmTime, i);
        edit.apply();
    }

    public static void setConcurso(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(main_Concurso, i);
        edit.apply();
    }

    public static void setConnectiontimeProgramationStartInMillis(Context context, long j) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putLong(Nauta_Online_ConnectionTimeProgramationStartInMillis, j);
        edit.apply();
    }

    public static void setConnectiontimeProgramationTimeInMillis(Context context, long j) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putLong(Nauta_Online_ConnectionTimeProgramationTimeInMillis, j);
        edit.apply();
    }

    public static void setIsCoberturaAlertShow(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Red_IsCoberturaAlertShow, z);
        edit.apply();
    }

    public static void setIsCoberturaSonar(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Red_IsCoberturaSonar, z);
        edit.apply();
    }

    public static void setIsCoberturaVibrar(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Red_IsCoberturaVibrar, z);
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

    public static void setRelojPosicionX(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(Config_service_reloj_posicion_x, i);
        edit.apply();
    }

    public static void setRelojPosicionY(Context context, int i) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putInt(Config_service_reloj_posicion_y, i);
        edit.apply();
    }

    public static void setUltimoEnvio(Context context, long j) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putLong(SALDO_ULTIMOENVIO, j);
        edit.apply();
    }

    public static void setWidgetOnBlanco(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Config_WidgetOnBlanco, z);
        edit.apply();
    }

    public static void setWidgetOnNegro(Context context, boolean z) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Config_noDatabase_File, 0).edit();
        edit.putBoolean(Config_WidgetOnNegro, z);
        edit.apply();
    }
}
