package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.marlon.portalusuario.domain.model.AppSettings
import com.marlon.portalusuario.domain.model.ModeNight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val AppPreferencesName = "APP_PREFERENCES"
private val Context.dataStore by preferencesDataStore(name = AppPreferencesName)

class AppPreferencesManager(private val context: Context) {
    fun preferences(): Flow<AppSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val skippedLogin = preferences[AppPreferencesKeys.SKIPPED_LOGIN] ?: false
            val modeNight = (preferences[AppPreferencesKeys.MODE_NIGHT] ?: "FOLLOW_SYSTEM").let { ModeNight.valueOf(it) }
            val isShowingTrafficBubble = preferences[AppPreferencesKeys.IS_SHOWING_TRAFFIC_BUBBLE] ?: false
            val isIntroOpened = preferences[AppPreferencesKeys.IS_INTRO_OPENED] ?: false

            AppSettings(
                skipLogin = skippedLogin,
                modeNight = modeNight,
                isShowingTrafficBubble = isShowingTrafficBubble,
                isIntroOpened = isIntroOpened
            )
        }

    suspend fun updateSkippedLogin(skippedLogin: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.SKIPPED_LOGIN] = skippedLogin
        }
    }

    suspend fun updateModeNight(modeNight: ModeNight) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.MODE_NIGHT] = modeNight.name
        }
    }

    suspend fun updateIsShowingTrafficBubble(isShowingTrafficBubble: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_SHOWING_TRAFFIC_BUBBLE] = isShowingTrafficBubble
        }
    }

    suspend fun updateIsIntroOpened(isIntroOpened: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_INTRO_OPENED] = isIntroOpened
        }
    }
}

private object AppPreferencesKeys {
    val SKIPPED_LOGIN = booleanPreferencesKey("SKIPPED_LOGIN")
    val MODE_NIGHT = stringPreferencesKey("MODE_NIGHT")
    val IS_SHOWING_TRAFFIC_BUBBLE = booleanPreferencesKey("IS_SHOWING_TRAFFIC_BUBBLE")
    val IS_INTRO_OPENED = booleanPreferencesKey("IS_INTRO_OPENED")
}
