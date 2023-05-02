package com.marlon.portalusuario.util

import android.content.Context
import android.content.SharedPreferences

class Pref(val context: Context) {

    private val storage: SharedPreferences = context.getSharedPreferences("nauta_session", 0)

    var currentUserId: Int
        set(value) {
            storage.edit().putInt("current_user_id", value).apply()
        }
        get() {
             return storage.getInt("current_user_id", 0)
        }

    var reservedTime: Int
        set(value) {
            storage.edit().putInt("reserved_time", value).apply()
        }
        get() {
            return storage.getInt("reserved_time", 0)
        }

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

    fun removeSession(dataSession: Map<String, String>) {
        for (key in dataSession.keys) {
            storage.edit().remove(key).apply()
        }
    }

    fun saveLastUserUpdate(value: String) {
        storage.edit().putString("last_portal_nauta_update", value).apply()
    }

    fun getLastUserUpdate(): String {
        return storage.getString("last_portal_nauta_update", "") ?: ""
    }
}