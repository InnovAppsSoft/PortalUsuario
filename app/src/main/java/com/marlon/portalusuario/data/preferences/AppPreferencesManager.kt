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

private const val APP_PREFERENCES_NAME = "APP_PREFERENCES"
private val Context.dataStore by preferencesDataStore(name = APP_PREFERENCES_NAME)

/**
 * Manages application preferences using Android DataStore.
 *
 * This class provides methods to read and update various application settings, such as:
 * - Night mode (light, dark, follow system).
 * - Visibility of the traffic bubble.
 * - Visibility of account balance on the traffic bubble.
 * - Visibility of data balance on the traffic bubble.
 * - Whether the intro screen has been opened.
 *
 * The preferences are stored using the DataStore mechanism, providing a type-safe and asynchronous
 * way to persist data.
 *
 * @property context The application context, required for accessing DataStore.
 */
open class AppPreferencesManager(
    private val context: Context,
) : IAppPreferencesManager {
    override fun preferences(): Flow<AppSettings> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                val modeNight =
                    (preferences[AppPreferencesKeys.MODE_NIGHT] ?: "FOLLOW_SYSTEM").let {
                        ModeNight.valueOf(
                            it,
                        )
                    }
                val isDynamicColor = preferences[AppPreferencesKeys.IS_DYNAMIC_COLOR] ?: true
                val isShowingTrafficBubble = preferences[AppPreferencesKeys.IS_SHOWING_TRAFFIC_BUBBLE] ?: false
                val isShowingAccountBalanceOnTrafficBubble =
                    preferences[AppPreferencesKeys.IS_SHOWING_ACCOUNT_BALANCE_ON_TRAFFIC_BUBBLE] ?: false
                val isShowingDataBalanceOnTrafficBubble =
                    preferences[AppPreferencesKeys.IS_SHOWING_DATA_BALANCE_ON_TRAFFIC_BUBBLE] ?: false
                val isShowingTrafficSpeedBubble =
                    preferences[AppPreferencesKeys.IS_SHOWING_TRAFFIC_SPEED_BUBBLE] ?: false
                val isIntroOpened = preferences[AppPreferencesKeys.IS_INTRO_OPENED] ?: false

                AppSettings(
                    modeNight = modeNight,
                    isDynamicColor = isDynamicColor,
                    isShowingTrafficBubble = isShowingTrafficBubble,
                    isShowingAccountBalanceOnTrafficBubble = isShowingAccountBalanceOnTrafficBubble,
                    isShowingDataBalanceOnTrafficBubble = isShowingDataBalanceOnTrafficBubble,
                    isShowingTrafficSpeedBubble = isShowingTrafficSpeedBubble,
                    isIntroOpened = isIntroOpened,
                )
            }

    override suspend fun updateIsShowingAccountBalanceOnTrafficBubble(isShowingAccountBalanceOnTrafficBubble: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_SHOWING_ACCOUNT_BALANCE_ON_TRAFFIC_BUBBLE] =
                isShowingAccountBalanceOnTrafficBubble
        }
    }

    override suspend fun updateIsShowingDataBalanceOnTrafficBubble(isShowingDataBalanceOnTrafficBubble: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_SHOWING_DATA_BALANCE_ON_TRAFFIC_BUBBLE] =
                isShowingDataBalanceOnTrafficBubble
        }
    }

    override suspend fun updateModeNight(modeNight: ModeNight) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.MODE_NIGHT] = modeNight.name
        }
    }

    override suspend fun updateIsDynamicColor(isDynamicColor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_DYNAMIC_COLOR] = isDynamicColor
        }
    }

    override suspend fun updateIsShowingTrafficBubble(isShowingTrafficBubble: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_SHOWING_TRAFFIC_BUBBLE] = isShowingTrafficBubble
        }
    }

    override suspend fun updateIsShowingTrafficSpeedBubble(isShowingTrafficSpeedBubble: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_SHOWING_TRAFFIC_SPEED_BUBBLE] = isShowingTrafficSpeedBubble
        }
    }

    override suspend fun updateIsIntroOpened(isIntroOpened: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AppPreferencesKeys.IS_INTRO_OPENED] = isIntroOpened
        }
    }
}

private object AppPreferencesKeys {
    val MODE_NIGHT = stringPreferencesKey("MODE_NIGHT")
    val IS_DYNAMIC_COLOR = booleanPreferencesKey("IS_DYNAMIC_COLOR")
    val IS_SHOWING_TRAFFIC_BUBBLE = booleanPreferencesKey("IS_SHOWING_TRAFFIC_BUBBLE")
    val IS_SHOWING_ACCOUNT_BALANCE_ON_TRAFFIC_BUBBLE =
        booleanPreferencesKey("IS_SHOWING_ACCOUNT_BALANCE_ON_TRAFFIC_BUBBLE")
    val IS_SHOWING_DATA_BALANCE_ON_TRAFFIC_BUBBLE = booleanPreferencesKey("IS_SHOWING_DATA_BALANCE_ON_TRAFFIC_BUBBLE")
    val IS_SHOWING_TRAFFIC_SPEED_BUBBLE = booleanPreferencesKey("IS_SHOWING_TRAFFIC_SPEED_BUBBLE")
    val IS_INTRO_OPENED = booleanPreferencesKey("IS_INTRO_OPENED")
}
