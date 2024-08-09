package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.SlotIndexInfo
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val PreferencesName = "MOB_SERVICES"
private val Context.dataStore by preferencesDataStore(name = PreferencesName)

class MobServicesPreferences(private val context: Context) {
    val preferences = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[ServicesPreferencesKey.SLOT_INDEX_INFO_LIST]?.let {
                MobServPreferences(Gson().fromJson(it, object : TypeToken<List<SlotIndexInfo>>() {}.type))
            } ?: MobServPreferences(emptyList())
        }

    suspend fun updateSlotIndexInfoList(slotIndexInfoList: List<SlotIndexInfo>) {
        context.dataStore.edit { preferences ->
            preferences[ServicesPreferencesKey.SLOT_INDEX_INFO_LIST] = Gson().toJson(slotIndexInfoList)
        }
    }
}

private object ServicesPreferencesKey {
    val SLOT_INDEX_INFO_LIST = stringPreferencesKey("SLOT_INDEX_INFO_LIST")
}
