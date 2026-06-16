package com.marlon.portalusuario.perfil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException

class ImageSaver(
    private val context: Context,
) {
    private var directoryName = "PortalUsuario"
    private var fileName = ""
    private var external = false

    fun setFileName(fileName: String): ImageSaver {
        this.fileName = fileName
        return this
    }

    fun setExternal(external: Boolean): ImageSaver {
        this.external = external
        return this
    }

    fun setDirectoryName(directoryName: String): ImageSaver {
        this.directoryName = directoryName
        return this
    }

    fun save(bitmapImage: Bitmap) {
        createFile().outputStream().use { outputStream ->
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
        }
    }

    fun load(): Bitmap? =
        try {
            createFile().inputStream().use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: IOException) {
            Log.e("ImageSaver", "Error loading image", e)
            null
        }

    fun deleteFile(): Boolean = createFile().delete()

    private fun createFile(): File {
        val directory =
            if (external) {
                getAlbumStorageDir(directoryName)
            } else {
                context.getDir(directoryName, Context.MODE_PRIVATE)
            }
        return File(directory, fileName)
    }

    private fun getAlbumStorageDir(albumName: String): File {
        val file =
            File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                albumName,
            )
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created")
        }
        return file
    }

    companion object {
        private const val JPEG_QUALITY = 50

        fun isExternalStorageWritable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }

        fun isExternalStorageReadable(): Boolean {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state ||
                Environment.MEDIA_MOUNTED_READ_ONLY == state
        }
    }
}
