package com.marlon.portalusuario.trafficbubble

import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

private const val BYTE_UNIT = 1024
private const val BYTE_UNIT_SI = 1000

object Util {
    @JvmStatic
    fun calcDownSpeed(timeTaken: Long, downBytes: Long): Long =
        if (timeTaken > 0) downBytes * BYTE_UNIT_SI / timeTaken else 0

    @JvmStatic
    fun calcUpSpeed(timeTaken: Long, upBytes: Long): Long =
        if (timeTaken > 0) upBytes * BYTE_UNIT_SI / timeTaken else 0

    @JvmStatic
    fun humanReadableByteCount(byteCount: Long, useSIUnits: Boolean = false): String {
        val byteUnit = if (useSIUnits) BYTE_UNIT_SI else BYTE_UNIT
        return when {
            byteCount < 0 -> "0 B"
            byteCount < byteUnit -> "$byteCount B"
            else -> {
                val exponent = (ln(byteCount.toDouble()) / ln(byteUnit.toDouble())).toInt()
                val prefix = "KMGTPE"[exponent - 1] + (if (useSIUnits) "" else "i")
                String.format(
                    Locale.getDefault(),
                    "%.1f %sB",
                    byteCount / byteUnit.toDouble().pow(exponent.toDouble()),
                    prefix
                )
            }
        }
    }
}
