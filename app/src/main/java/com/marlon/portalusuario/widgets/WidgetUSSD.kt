package com.marlon.portalusuario.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.marlon.portalusuario.R

class WidgetUSSD : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidgetUssd(context, appWidgetManager, appWidgetId)
            val intent = Intent(context, WidgetUSSD::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids =
                AppWidgetManager.getInstance(context.applicationContext)
                    .getAppWidgetIds(
                        ComponentName(context.applicationContext, WidgetUSSD::class.java),
                    )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

    override fun onEnabled(context: Context) {}

    override fun onDisabled(context: Context) {}
}

@Suppress("LongMethod")
internal fun updateAppWidgetUssd(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
) {
    val prefs = context.getSharedPreferences("cuentas", Context.MODE_PRIVATE)
    val saldo = prefs.getString("saldo", "0.00 CUP") ?: "0.00 CUP"
    val nacionales = prefs.getString("datos_nacionales", "0 MB") ?: "0 MB"
    val datos = prefs.getString("paquete", "0 MB") ?: "0 MB"
    val lte = prefs.getString("lte", "0 MB") ?: "0 MB"
    val venceSaldo = prefs.getString("vence_saldo", "00/00/00") ?: "00/00/00"
    val venceDatos = prefs.getString("vence_datos", "0 días") ?: "0 días"

    val views = RemoteViews(context.packageName, R.layout.widget_u_s_s_d)
    views.setTextViewText(R.id.widgetsaldo, saldo)
    views.setTextViewText(R.id.widgetnacionales, nacionales)
    views.setTextViewText(R.id.widgetdatos, datos)
    views.setTextViewText(R.id.widgetlte, lte)
    views.setTextViewText(R.id.widgetvencesaldo, venceSaldo)
    views.setTextViewText(R.id.widgetvencenacionales, venceDatos)
    views.setTextViewText(R.id.widgetvencedatos, venceDatos)
    views.setTextViewText(R.id.widgetvencenlte, venceDatos)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
