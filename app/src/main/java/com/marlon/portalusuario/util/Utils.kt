package com.marlon.portalusuario.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Base64
import android.util.Log
import androidx.core.content.ContextCompat
import com.auth0.android.jwt.JWT
import com.caverock.androidsvg.SVG
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
private const val TAG = "Utils"
object Utils {
    fun Context.hasPermissions(vararg permissions: String): Boolean {
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    fun String.toBitmap(): Bitmap? = runCatching {
        return@runCatching SVG.getFromString(this)?.let { svg ->
            // Obtener el ancho y el alto del documento SVG
            val width = svg.documentWidth.toInt()
            val height = svg.documentHeight.toInt()

            // Crear un bitmap en blanco con las dimensiones obtenidas
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Crear un canvas para dibujar en el bitmap
            val canvas = Canvas(bitmap)

            // Renderizar el SVG en el canvas
            svg.renderToCanvas(canvas)
            bitmap
        }
    }.getOrNull()

    fun String.fixDateFormat(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).let { format ->
            format.parse(this).let { format.format(it!!) }
        }

    fun String.isTokenExpired(): Boolean = JWT(this).expiresAt.let {
        it != null && Date().after(it)
    }

    fun createPasswordApp(): String {
        // Crea un objeto SimpleDateFormat para formatear la fecha actual
        val dateFormatter = SimpleDateFormat("ddMMyyyyHH", Locale.getDefault())
        // Obtiene la fecha actual como una cadena en el formato "ddMMyyyyHH"
        val dateString = dateFormatter.format(Date())

        // Construye la cadena de clave de la aplicación
        val appKey = "portal" + dateString + "externalPortal"
        // Convierte la cadena de clave de la aplicación en un objeto ByteArray
        val appKeyData = appKey.toByteArray(Charsets.UTF_8)

        // Calcula el hash SHA-512 de la cadena de clave de la aplicación
        val hashedData = MessageDigest.getInstance("SHA-512").digest(appKeyData)
        // Codifica el hash en formato Base64 y devuelve la cadena resultante
        return "ApiKey ${Base64.encodeToString(hashedData, Base64.NO_WRAP)}"
    }

    fun String.parseLastUpdated(): Long {
        val calendar = Calendar.getInstance()
        calendar.time = SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault()).parse(this)!!
        return calendar.timeInMillis
    }

    fun Long.isAtLeastOneHourElapsed(): Boolean {
        Log.d(TAG, "isAtLeastOneHourElapsed: timestampMillis: $this")
        val oneHourMillisThreshold = 3600 * 1000
        Log.d(TAG, "isAtLeastOneHourElapsed: currentTimeMillis: ${System.currentTimeMillis()}")
        val millisSinceTimestamp = System.currentTimeMillis() - this
        Log.d(TAG, "isAtLeastOneHourElapsed: millisSinceTimestamp: $millisSinceTimestamp")
        return millisSinceTimestamp >= oneHourMillisThreshold
    }
}
