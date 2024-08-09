package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.marlon.portalusuario.domain.model.AppPreferences
import com.marlon.portalusuario.domain.model.SimPaired
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val AppPreferencesName = "APP_PREFERENCES"
private val Context.dataStore by preferencesDataStore(name = AppPreferencesName)

class AppPreferences(private val context: Context) {
    fun preferences(): Flow<AppPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val mobileServiceSelectedId = preferences[AppPreferencesKeys.MOBILE_SERVICE_SELECTED_ID]
            val simPaired = preferences[AppPreferencesKeys.SIMS_PAIRED]?.let {
                Gson().fromJson(it, Array<SimPaired>::class.java).toList()
            } ?: emptyList()
            val skippedLogin = preferences[AppPreferencesKeys.SKIPPED_LOGIN] ?: false

            AppPreferences(mobileServiceSelectedId, simPaired, skippedLogin)
        }

    suspend fun updateMobileServiceSelectedId(id: String?) {
        context.dataStore.edit { preferences ->
            id?.let { preferences[AppPreferencesKeys.MOBILE_SERVICE_SELECTED_ID] = it }
                ?: preferences.remove(AppPreferencesKeys.MOBILE_SERVICE_SELECTED_ID)
        }
    }

    suspend fun updateIsSimCardsPaired(simsPaired: List<SimPaired>) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.SIMS_PAIRED] = Gson().toJson(simsPaired)
        }
    }

    suspend fun updateSkippedLogin(skippedLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.SKIPPED_LOGIN] = skippedLogin
        }
    }
}

private object AppPreferencesKeys {
    val MOBILE_SERVICE_SELECTED_ID = stringPreferencesKey("MOBILE_SERVICE_SELECTED_ID")
    val SIMS_PAIRED = stringPreferencesKey("SIMS_PAIRED")
    val SKIPPED_LOGIN = booleanPreferencesKey("SKIPPED_LOGIN")
}
