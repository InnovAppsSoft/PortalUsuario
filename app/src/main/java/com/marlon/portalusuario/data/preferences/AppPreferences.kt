package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.marlon.portalusuario.domain.model.AppPreferences
import com.marlon.portalusuario.domain.model.DataSession
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
            val dataSession = preferences[AppPreferencesKeys.DATA_SESSION]?.let {
                Gson().fromJson(it, DataSession::class.java)
            }
            val mobileServiceSelectedId = preferences[AppPreferencesKeys.MOBILE_SERVICE_SELECTED_ID]
            val simPaired = preferences[AppPreferencesKeys.SIMS_PAIRED]?.let {
                Gson().fromJson(it, Array<SimPaired>::class.java).toList()
            } ?: emptyList()

            AppPreferences(dataSession, mobileServiceSelectedId, simPaired)
        }

    suspend fun updateDataSession(dataSession: DataSession?) {
        context.dataStore.edit { preferences ->
            dataSession?.let { preferences[AppPreferencesKeys.DATA_SESSION] = Gson().toJson(it) }
                ?: preferences.remove(AppPreferencesKeys.DATA_SESSION)
        }
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
}

private object AppPreferencesKeys {
    val DATA_SESSION = stringPreferencesKey("DATA_SESSION")
    val MOBILE_SERVICE_SELECTED_ID = stringPreferencesKey("MOBILE_SERVICE_SELECTED_ID")
    val SIMS_PAIRED = stringPreferencesKey("SIMS_PAIRED")
}
