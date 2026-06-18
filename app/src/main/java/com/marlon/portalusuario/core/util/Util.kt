package com.marlon.portalusuario.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.GregorianCalendar
import java.util.Locale

object Util {
    @JvmStatic
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = cm?.activeNetworkInfo
        return (activeNetwork?.isConnectedOrConnecting) == true
    }

    @JvmStatic
    fun isConnectedByWifi(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnected == true
    }

    @JvmStatic
    fun isConnectedByMobileData(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.isConnected == true
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    fun date2String(date: GregorianCalendar): String =
        SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault()).format(date.time)

    @JvmStatic
    fun long2Date(timestamp: Long): GregorianCalendar = GregorianCalendar().apply { timeInMillis = timestamp }

    @JvmStatic
    fun currentDate2Long(): Long = GregorianCalendar().timeInMillis

    @JvmStatic
    fun roundDouble(numero: Double): Double =
        try {
            DecimalFormat("###.##").format(numero).toDouble()
        } catch (e: Exception) {
            0.0
        }
}
