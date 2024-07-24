package com.marlon.portalusuario.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.caverock.androidsvg.SVG
import java.text.SimpleDateFormat
import java.util.Locale

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
}
