package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.marlon.portalusuario.domain.model.AppPreferences
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
            val skippedLogin = preferences[AppPreferencesKeys.SKIPPED_LOGIN] ?: false

            AppPreferences(skippedLogin)
        }

    suspend fun updateSkippedLogin(skippedLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.SKIPPED_LOGIN] = skippedLogin
        }
    }
}

private object AppPreferencesKeys {
    val SKIPPED_LOGIN = booleanPreferencesKey("SKIPPED_LOGIN")
}
