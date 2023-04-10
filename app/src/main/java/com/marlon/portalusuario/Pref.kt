package com.marlon.portalusuario

import android.content.Context
import android.content.SharedPreferences

class Pref(val context: Context) {

    private val storage: SharedPreferences = context.getSharedPreferences("nauta_session", 0)

    fun saveSession(dataSession: Map<String, String>) {
        storage.edit().putString("username", dataSession["username"]).apply()
        storage.edit().putString("CSRFHW", dataSession["CSRFHW"]).apply()
        storage.edit().putString("wlanuserip", dataSession["wlanuserip"]).apply()
        storage.edit().putString("ATTRIBUTE_UUID", dataSession["ATTRIBUTE_UUID"]).apply()
    }

    fun getSession(): Map<String, String> {
        val username = storage.getString("username", null)
        username?.let {
            return mapOf(
                    "username" to username,
                    "CSRFHW" to storage.getString("CSRFHW", "")!!,
                    "wlanuserip" to storage.getString("wlanuserip", "")!!,
                    "ATTRIBUTE_UUID" to storage.getString("ATTRIBUTE_UUID", "")!!
            )
        }
        return mapOf()
    }
}