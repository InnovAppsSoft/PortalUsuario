package com.marlon.portalusuario.widgets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.marlon.portalusuario.R
import com.marlon.portalusuario.activities.MainActivity

/**
 * Implementation of App Widget functionality.
 */
class Widget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

@SuppressLint("UnspecifiedImmutableFlag")
internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.widget)

    val pendingIntentSaldo = PendingIntent.getActivity(context, 0,
        Intent(Intent.ACTION_CALL, Uri.parse("tel:*222${Uri.encode("#")}")), 0)

    val pendingIntentBono = PendingIntent.getActivity(context, 0,
        Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*266${Uri.encode("#")}")), 0)

    val pendingIntentDatos = PendingIntent.getActivity(context, 0,
        Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*328${Uri.encode("#")}")), 0)

    val pendingIntentVoz = PendingIntent.getActivity(context, 0,
        Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*869${Uri.encode("#")}")), 0)

    val pendingIntentSms = PendingIntent.getActivity(context, 0,
        Intent(Intent.ACTION_CALL, Uri.parse("tel:*222*767${Uri.encode("#")}")), 0)

    views.setOnClickPendingIntent(R.id.widget_button_saldo, pendingIntentSaldo)
    views.setOnClickPendingIntent(R.id.widget_button_bonos, pendingIntentBono)
    views.setOnClickPendingIntent(R.id.widget_button_saldo_datos, pendingIntentDatos)
    views.setOnClickPendingIntent(R.id.widget_button_saldo_voz, pendingIntentVoz)
    views.setOnClickPendingIntent(R.id.widget_button_saldo_sms, pendingIntentSms)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}