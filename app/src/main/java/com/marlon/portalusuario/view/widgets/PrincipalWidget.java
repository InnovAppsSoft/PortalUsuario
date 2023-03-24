package com.marlon.portalusuario.view.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.marlon.portalusuario.R;
import com.marlon.portalusuario.view.activities.LlamadaOcultoActivity;
import com.marlon.portalusuario.view.activities.Llamada_99Activity;
import com.marlon.portalusuario.firewall.ActivityMain;
import com.marlon.portalusuario.util.CodigosUSSD;

public class PrincipalWidget extends AppWidgetProvider {
    private static SharedPreferences preferences;

    public void onDisabled(Context context) {
    }

    public void onEnabled(Context context) {

    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int i) {

        Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:*222" + CodigosUSSD.encodeHash));
        Intent intent2 = new Intent("android.intent.action.CALL", Uri.parse("tel:*222*266" + CodigosUSSD.encodeHash));
        Intent intent1 = new Intent(context, PrincipalWidget.class);

        PendingIntent activity1 = PendingIntent.getActivity(context, 0, intent, 0);
        PendingIntent activity2 = PendingIntent.getActivity(context, 0, new Intent(context, ActivityMain.class), 0);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent1, 0);
        PendingIntent activity3 = PendingIntent.getActivity(context, 0, new Intent(context, Llamada_99Activity.class), 0);
        PendingIntent activity4 = PendingIntent.getActivity(context, 0, getIntent(context), 0);
        PendingIntent activity5 = PendingIntent.getActivity(context, 0, new Intent(context, ActivityMain.class), 0);
        PendingIntent activity6 = PendingIntent.getActivity(context, 0, new Intent(context, LlamadaOcultoActivity.class), 0);
        PendingIntent activity7 = PendingIntent.getActivity(context, 0, intent2, 0);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), (int) R.layout.widget_v1);
        remoteViews.setOnClickPendingIntent(R.id.linearLayoutSPPrincipalWidget, activity1);
        remoteViews.setOnClickPendingIntent(R.id.linearLayoutETPrincipalWidget, activity2);
        remoteViews.setOnClickPendingIntent(R.id.widgetasterisco, activity3);
        remoteViews.setOnClickPendingIntent(R.id.probarinternet, activity4);

        remoteViews.setOnClickPendingIntent(R.id.numeroprivado, activity6);
        remoteViews.setOnClickPendingIntent(R.id.bonos, activity7);

        remoteViews.setOnClickPendingIntent(R.id.cardview,broadcast);
        remoteViews.setOnClickPendingIntent(R.id.linearLayoutETPrincipalWidget,activity5);

        {

        }

        appWidgetManager.updateAppWidget(i, remoteViews);
    }


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] iArr) {
        for (int i : iArr) {
            updateAppWidget(context, appWidgetManager, i);
        }
    }

    public static Intent getIntent(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        try {
            if (Build.VERSION.SDK_INT >= 30) {
                intent.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
            } else {
                intent.setClassName("com.android.settings", "com.android.settings.RadioInfo");
            }
        } catch (Exception unused) {
            Toast.makeText(context, "Su dispositivo no admite esta funcionalidad, lamentamos las molestias :(", Toast.LENGTH_SHORT).show();
        }
        return intent;
    }
}