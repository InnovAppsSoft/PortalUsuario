package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.marlon.portalusuario.domain.model.ServicesPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val servicesPreference = "SERVICES_PREFERENCE"
private val Context.dataStore by preferencesDataStore(name = servicesPreference)

class ServicesPreference(private val context: Context) {
    fun preferences(): Flow<ServicesPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { value: Preferences ->
            val currentMobileService = value[PreferencesKeys.CURRENT_MOBILE_SERVICE] ?: ""
            val tokenAuth = value[PreferencesKeys.TOKEN_AUTH] ?: ""

            ServicesPreferences(currentMobileService, tokenAuth)
        }
    suspend fun updateCurrentMobileService(currentMobileService: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_MOBILE_SERVICE] = currentMobileService
        }
    }
    suspend fun updateTokenAuth(tokenAuth: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN_AUTH] = tokenAuth
        }
    }
}

private object PreferencesKeys {
    val CURRENT_MOBILE_SERVICE = stringPreferencesKey("CURRENT_MOBILE_SERVICE")
    val TOKEN_AUTH = stringPreferencesKey("TOKEN_AUTH")
}
