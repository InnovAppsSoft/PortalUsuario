package com.marlon.portalusuario.data

import java.util.Calendar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow

private const val SECONDS_IN_HOUR = 3600
private const val SECONDS_IN_MINUTE = 60
private const val SIZE_UNIT_MAX_LENGTH = 1024.0
private const val HOURS_PER_DAY = 24
private const val MINUTES_PER_HOUR = 60
private const val MILLISECONDS = 1000
private const val DEFAULT_DATE_FORMAT = "dd/MM/yyyy"

val Long.asRemainingDays
    get(): Int {
        val calendar = Calendar.getInstance()
        val diffInMillis = this - calendar.timeInMillis
        return (diffInMillis / (HOURS_PER_DAY * MINUTES_PER_HOUR * MINUTES_PER_HOUR * MILLISECONDS) + 1).toInt()
    }

val Int.asFutureDate
    get(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, this)

        return SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    }

val String.isActive get() = this != "no activos"

@Throws(ParseException::class)
fun String.asDate(pattern: String = DEFAULT_DATE_FORMAT): Date? =
    SimpleDateFormat(pattern, Locale.getDefault()).parse(this)

@get:Throws(ParseException::class)
val String.asDateMillis
    get() = asDate()?.let {
        val calendar = Calendar.getInstance()
        calendar.time = it
        calendar.timeInMillis
    }

val String.asBytes
    get(): Long {
        val count = this.replace("[GMKBT]".toRegex(), "")
        val unit = this.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[
            this.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray().size - 1
        ].uppercase(Locale.getDefault())
        return (count.toDouble() * 1024.0.pow("BKMGT".indexOf(unit[0]).toDouble())).toLong()
    }

val Long.asDateString
    get(): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    }

val Long.asTimeString
    get(): String {
        val hours = this / SECONDS_IN_HOUR
        val minutes = this % SECONDS_IN_HOUR / SECONDS_IN_MINUTE
        val seconds = this % SECONDS_IN_MINUTE
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

val Long.asSizeString
    get(): String {
        val sizeUnits = arrayOf("bytes", "KB", "MB", "GB", "TB")
        var sizeValue = this.toDouble()
        var sizeUnitIndex = 0
        while (sizeValue >= SIZE_UNIT_MAX_LENGTH && sizeUnitIndex < sizeUnits.size - 1) {
            sizeValue /= SIZE_UNIT_MAX_LENGTH
            sizeUnitIndex++
        }
        return String.format(Locale.getDefault(), "%.2f %s", sizeValue, sizeUnits[sizeUnitIndex])
    }

val String.asSeconds
    get(): Long {
        val parts = this.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var totalSeconds: Long = 0
        for (part in parts) {
            totalSeconds = totalSeconds * SECONDS_IN_MINUTE + part.toLong()
        }
        return totalSeconds
    }
