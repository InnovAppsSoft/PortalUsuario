package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appDataStore by preferencesDataStore(name = "app_shared_preferences")

object AppDataStoreKeys {
    val SHOW_NOTIFICATIONS = booleanPreferencesKey("show_notifications")
    val SAVE_LOGS = booleanPreferencesKey("save_logs")
    val STORAGE_ADS = booleanPreferencesKey("storage_ads")
    val NOTIFICATIONS_COUNT = intPreferencesKey("notifications_count")
}

fun Context.showNotificationsFlow(): Flow<Boolean> =
    appDataStore.data.map { it[AppDataStoreKeys.SHOW_NOTIFICATIONS] ?: true }

fun Context.saveLogsFlow(): Flow<Boolean> = appDataStore.data.map { it[AppDataStoreKeys.SAVE_LOGS] ?: true }

fun Context.storageAdsFlow(): Flow<Boolean> = appDataStore.data.map { it[AppDataStoreKeys.STORAGE_ADS] ?: true }

fun Context.notificationsCountFlow(): Flow<Int> =
    appDataStore.data.map { it[AppDataStoreKeys.NOTIFICATIONS_COUNT] ?: 0 }

suspend fun Context.setShowNotifications(enabled: Boolean) {
    appDataStore.edit { it[AppDataStoreKeys.SHOW_NOTIFICATIONS] = enabled }
}

suspend fun Context.setSaveLogs(enabled: Boolean) {
    appDataStore.edit { it[AppDataStoreKeys.SAVE_LOGS] = enabled }
}

suspend fun Context.setStorageAds(enabled: Boolean) {
    appDataStore.edit { it[AppDataStoreKeys.STORAGE_ADS] = enabled }
}

suspend fun Context.setNotificationsCount(count: Int) {
    appDataStore.edit { it[AppDataStoreKeys.NOTIFICATIONS_COUNT] = count }
}
