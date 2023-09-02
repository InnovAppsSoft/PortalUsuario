package com.marlon.portalusuario.feature.profile.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ImageSaver @Inject constructor(@ApplicationContext private val context: Context) {
    private var directoryName = "PortalUsuario"
    private var fileName = ""
    private var external = false

    /**
     * Sets the file name for the ImageSaver.
     *
     * @param fileName the name of the file
     * @return the ImageSaver object
     */
    fun setFileName(fileName: String): ImageSaver {
        this.fileName = fileName
        return this
    }

    /**
     * Sets the flag indicating if the image is external.
     *
     * @param external true if the image is external, false otherwise
     * @return the ImageSaver instance
     */
    fun setExternal(external: Boolean): ImageSaver {
        this.external = external
        return this
    }

    /**
     * Sets the directory name where the image will be saved.
     *
     * @param directoryName The name of the directory.
     * @return The ImageSaver object.
     */
    fun setDirectoryName(directoryName: String): ImageSaver {
        this.directoryName = directoryName
        return this
    }

    /**
     * Saves the given bitmap image to a file.
     *
     * @param bitmapImage The bitmap image to be saved.
     */
    fun save(bitmapImage: Bitmap) {
        var fileOutputStream: FileOutputStream? = null
        try {
            // Create a file for saving the image
            fileOutputStream = FileOutputStream(createFile())

            // Compress the bitmap image and write it to the file
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, fileOutputStream)
        } catch (e: IOException) {
            Log.e("ImageSaver", "Error saving image", e)
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: IOException) {
                Log.e("ImageSaver", "Error saving image", e)
            }
        }
    }

    /**
     * Creates a file in the specified directory.
     * If external is true, the file is created in the external storage directory.
     * If external is false, the file is created in the internal storage directory.
     *
     * @return The created file.
     */
    private fun createFile(): File {

        // Check if the file should be created in the external storage directory
        val directory: File = if (external) {
            getAlbumStorageDir(directoryName)
        } else {
            context.getDir(directoryName, Context.MODE_PRIVATE)
        }

        // Return the created file
        return File(directory, fileName)
    }

    /**
     * Deletes a file.
     *
     * @return true if the file was successfully deleted, false otherwise.
     */
    fun deleteFile(): Boolean {
        // Create a file
        val file = createFile()

        // Delete the file
        return file.delete()
    }

    /**
     * Returns the file object representing the album storage directory.
     * If the directory does not exist, it will be created.
     *
     * @param albumName The name of the album.
     * @return The file object representing the album storage directory.
     */
    private fun getAlbumStorageDir(albumName: String): File {
        // Get the external storage public directory for pictures
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        // Create a file object for the album storage directory
        val albumDir = File(storageDir, albumName)

        // If the directory does not exist, create it
        if (!albumDir.mkdirs()) {
            // Log an error if the directory was not created
            Log.e("ImageSaver", "Directory not created")
        }

        // Return the file object representing the album storage directory
        return albumDir
    }

    /**
     * Load a bitmap from a file.
     *
     * @return The loaded bitmap, or null if an error occurred.
     */
    fun load(): Bitmap? {
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(createFile())
            return BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            Log.e("ImageSaver", "Error loading image", e)
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Log.e("ImageSaver", "Error loading image", e)
            }
        }
        return null
    }

    companion object {
        const val COMPRESSION_QUALITY = 50
        val isExternalStorageWritable: Boolean
            /**
             * Checks if the external storage is writable.
             *
             * @return true if the external storage is writable, false otherwise.
             */
            get() {
                // Get the state of the external storage
                val state = Environment.getExternalStorageState()

                // Check if the external storage is mounted and writable
                return Environment.MEDIA_MOUNTED == state
            }
        val isExternalStorageReadable: Boolean
            /**
             * Check if the external storage is readable.
             *
             * @return true if the external storage is readable, false otherwise.
             */
            get() {
                // Get the state of the external storage
                val state = Environment.getExternalStorageState()

                // Return true if the external storage is mounted or mounted as read-only
                return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
            }
    }
}
