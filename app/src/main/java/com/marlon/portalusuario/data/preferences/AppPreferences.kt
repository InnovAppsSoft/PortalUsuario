package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.marlon.portalusuario.domain.model.AppPreferences
import com.marlon.portalusuario.domain.model.DataSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val appPreferences = "APP_PREFERENCES"
private val Context.dataStore by preferencesDataStore(name = appPreferences)

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
            } ?: DataSession()

            AppPreferences(dataSession)
        }

    suspend fun updateDataSession(dataSession: DataSession) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.DATA_SESSION] = Gson().toJson(dataSession)
        }
    }
}

private object AppPreferencesKeys {
    val DATA_SESSION = stringPreferencesKey("DATA_SESSION")
}
