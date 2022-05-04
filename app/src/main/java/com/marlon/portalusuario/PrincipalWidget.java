package com.marlon.portalusuario;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

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
        PendingIntent activity4 = PendingIntent.getActivity(context, 0, new Intent(context, SetLTEModeActivity.class), 0);
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
}