package com.marlon.portalusuario.linea;

import android.content.Context;
import android.content.res.Resources;

import com.marlon.portalusuario.R;

public class g {
    public static String a(String str, Context context) {
        Resources resources;
        int i;
        String string = context.getResources().getString(R.string.desconocido);
        if (str.equalsIgnoreCase("cu")) {
            resources = context.getResources();
            i = R.string.otros_wifi_regiones_cu;
        } else if (str.equalsIgnoreCase("us")) {
            resources = context.getResources();
            i = R.string.otros_wifi_regiones_us;
        } else if (str.equalsIgnoreCase("jp")) {
            resources = context.getResources();
            i = R.string.otros_wifi_regiones_jp;
        } else if (str.equalsIgnoreCase("eu")) {
            resources = context.getResources();
            i = R.string.otros_wifi_regiones_eu;
        } else if (!str.equalsIgnoreCase("ru")) {
            return string;
        } else {
            resources = context.getResources();
            i = R.string.otros_wifi_regiones_ru;
        }
        return resources.getString(i);
    }
}