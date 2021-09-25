package com.marlon.portalusuario.usodedatos.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class DateUtils {
    companion object{
        var TAG = "DATE_UTILS"

        @SuppressLint("SimpleDateFormat")
        fun getDayID() : String{

            var timestamp = System.currentTimeMillis()
            var formatter = SimpleDateFormat("dd-MMM-yyyy")
            return formatter.format(timestamp)
        }
    }
}