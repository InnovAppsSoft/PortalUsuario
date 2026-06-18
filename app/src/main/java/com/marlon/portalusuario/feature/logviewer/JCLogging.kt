package com.marlon.portalusuario.feature.logviewer

import android.content.Context
import android.os.Environment
import android.util.Log
import com.marlon.portalusuario.data.preferences.saveLogsFlow
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

object JCLogging {
    private const val LOG_TITLE = "PortalUsuarioLog"
    private val DATE_FORMAT = SimpleDateFormat("dd-MM-yyyy | HH:mm:ss.SSS", Locale.getDefault())
    private const val LOGGER_NAME = "com.marlon.portalusuario.erroreslog.JCLogging"
    private var writer: PrintWriter? = null
    private const val PRINT_ON_LOGCAT = true
    private const val PRINT_ON_FILE = true
    private var context: Context? = null

    fun init(context: Context) {
        JCLogging.context = context
        setWriter()
    }

    fun createFile(path: String): Boolean {
        return try {
            val logFile = getFile()
            if (!logFile.exists()) return false
            FileWriter(logFile).close()
            true
        } catch (ex: IOException) {
            error(null, null, ex)
            false
        }
    }

    fun getDirectory(): String =
        context?.let {
            it.getExternalFilesDir(Environment.getDataDirectory().absolutePath)?.absolutePath ?: ""
        } ?: ""

    fun readFromFile(file: File): List<String> {
        val lines = mutableListOf<String>()
        val reader = BufferedReader(FileReader(file))
        reader.use { br ->
            while (true) {
                val line = br.readLine() ?: break
                lines.add(line)
            }
        }
        return lines
    }

    fun readAllFromFile(file: File): String {
        val reader = BufferedReader(FileReader(file))
        reader.use { br ->
            val sb = StringBuilder()
            while (true) {
                val line = br.readLine() ?: break
                sb.append(line)
            }
            return sb.toString()
        }
    }

    fun getFile(): File = File(getDirectory(), "log.txt")

    fun clearLog() {
        try {
            FileWriter(getFile()).use { it.write("") }
        } catch (e: IOException) {
            e.printStackTrace()
            error(null, null, e)
        }
    }

    private fun setWriter() {
        try {
            writer = PrintWriter(BufferedWriter(FileWriter(getFile(), true)), true)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun fileExists(): Boolean {
        return try {
            val file = getFile()
            if (!file.exists()) return false
            FileWriter(file).close()
            true
        } catch (ex: IOException) {
            error(null, null, ex)
            false
        }
    }

    fun error(
        msg: String?,
        obj: Any?,
        throwable: Throwable?,
    ) = throwMessage('E', msg, obj, throwable)

    fun message(
        msg: String?,
        obj: Any?,
    ) = throwMessage('I', msg, obj, null)

    fun warning(
        msg: String?,
        obj: Any?,
    ) = throwMessage('W', msg, obj, null)

    @Suppress("UnusedPrivateMember")
    private fun throwMessage(
        type: Char,
        msg: String?,
        obj: Any?,
        th: Throwable?,
    ) {
        var resolvedMsg = msg
        var resolvedObj = obj
        if ((resolvedMsg == null || resolvedObj == null) && (PRINT_ON_FILE || PRINT_ON_LOGCAT)) {
            val stackTrace = Throwable().stackTrace
            for (element in stackTrace) {
                val className = element.className
                if (className != LOGGER_NAME) {
                    if (resolvedMsg == null) {
                        resolvedMsg = className.substringAfterLast('.')
                    }
                    if (resolvedObj == null) {
                        resolvedObj = "${element.methodName}()"
                    }
                }
            }
        }
        if (PRINT_ON_FILE) {
            val ctx = context ?: return
            try {
                if (runBlocking { ctx.saveLogsFlow().first() }) {
                    val formatString = "%s | %c | %s: %s\n"
                    writer?.format(formatString, DATE_FORMAT.format(Date()), type, resolvedMsg, resolvedObj)
                    if (type == 'E' && th != null) {
                        th.printStackTrace(writer)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (PRINT_ON_LOGCAT) {
            val str2 = "$resolvedMsg: $resolvedObj"
            when (type) {
                'E' -> if (th == null) Log.e(LOG_TITLE, str2) else Log.e(LOG_TITLE, str2, th)
                'I' -> Log.i(LOG_TITLE, str2)
                'W' -> Log.w(LOG_TITLE, str2)
            }
        }
    }
}
