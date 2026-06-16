package com.marlon.portalusuario.util

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.media.MediaScannerConnection
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val IMAGE_QUALITY = 85

fun Bitmap.saveToGallery(context: Context) {
    val folderName = "/PortaUsuario"
    val fileName = "imagen"
    val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!, folderName)
    val dateStamp = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US).format(Calendar.getInstance().time)

    if (!dir.exists()) dir.mkdirs()

    val file = File(dir, "$fileName$dateStamp.png")

    try {
        FileOutputStream(file).use { fOut ->
            compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fOut)
            fOut.flush()
        }
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null) { _: String, _: android.net.Uri? -> }
        Toast.makeText(context, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "¡No se ha podido guardar la imagen!", Toast.LENGTH_SHORT).show()
    }
}
