package com.marlon.portalusuario.a;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Guarda {
    static SharedPreferences a;

    public static String a(Context context, String str, String str2) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        a = defaultSharedPreferences;
        return defaultSharedPreferences.getString(str, str2);
    }
}
