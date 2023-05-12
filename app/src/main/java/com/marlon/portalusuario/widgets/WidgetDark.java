package com.marlon.portalusuario.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.marlon.portalusuario.R;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetDark extends AppWidgetProvider {

    static SharedPreferences sp_cuentas;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        sp_cuentas = context.getSharedPreferences("cuentas", Context.MODE_PRIVATE);
        String saldo = sp_cuentas.getString("saldo", "0.00 CUP");
        String nacionales = sp_cuentas.getString("datos_nacionales", "0 MB");
        String datos = sp_cuentas.getString("paquete", "0 MB");
        String lte = sp_cuentas.getString("lte", "0 MB");
        String vence_saldo = sp_cuentas.getString("vence_saldo", "00/00/00");
        String vence_datos = sp_cuentas.getString("vence_datos", "0 días");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_dark);

        views.setTextViewText(R.id.widgetsaldo, saldo);
        views.setTextViewText(R.id.widgetnacionales, nacionales);
        views.setTextViewText(R.id.widgetdatos, datos);
        views.setTextViewText(R.id.widgetlte, lte);
        views.setTextViewText(R.id.widgetvencesaldo,vence_saldo);
        views.setTextViewText(R.id.widgetvencenacionales, vence_datos);
        views.setTextViewText(R.id.widgetvencedatos, vence_datos);
        views.setTextViewText(R.id.widgetvencenlte,vence_datos);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);

            // update receiver widgets
            Intent intent = new Intent(context, WidgetDark.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = AppWidgetManager.getInstance(context.getApplicationContext())
                    .getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetDark.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);


        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}