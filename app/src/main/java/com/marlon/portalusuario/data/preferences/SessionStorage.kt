package com.marlon.portalusuario.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.marlon.portalusuario.domain.model.DataSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val StorageName = "SESSION_STORAGE"
private val Context.dataStore by preferencesDataStore(name = StorageName)

class SessionStorage(private val context: Context) {
    val dataSession: Flow<DataSession?> get() = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { storage ->
            storage[SessionStorageKeys.DATA_SESSION]?.let { Gson().fromJson(it, DataSession::class.java) }
        }

    suspend fun updateDataSession(dataSession: DataSession?) {
        context.dataStore.edit { storage ->
            dataSession?.let { storage[SessionStorageKeys.DATA_SESSION] = Gson().toJson(it) }
                ?: storage.remove(SessionStorageKeys.DATA_SESSION)
        }
    }
}

private object SessionStorageKeys {
    val DATA_SESSION = stringPreferencesKey("DATA_SESSION")
}
