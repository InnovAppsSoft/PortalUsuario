package com.marlon.portalusuario.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object Utils {
    fun Context.hasPermissions(vararg permissions: String): Boolean {
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }
}
