package com.marlon.portalusuario.feature.balancemanagement.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.marlon.portalusuario.feature.balancemanagement.domain.model.BalancePreferences
import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.BalanceManagementPreferencesDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val BALANCE_PREFERENCES_NAME = "balance_preferences"
private val Context.dataStore by preferencesDataStore(name = BALANCE_PREFERENCES_NAME)

@Singleton
class BalanceManagementPreferencesDataSourceImpl
@Inject constructor (
    @ApplicationContext private val context: Context
) : BalanceManagementPreferencesDataSource {
    override fun balancePreferences(): Flow<BalancePreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val currentSimCardId = preferences[BalanceManagementPreferencesKeys.CURRENT_SIM_CARD_ID] ?: ""
            BalancePreferences(currentSimCardId)
        }

    override suspend fun updateCurrentSimCardId(simCardId: String) {
        context.dataStore.edit { preferences ->
            preferences[BalanceManagementPreferencesKeys.CURRENT_SIM_CARD_ID] = simCardId
        }
    }
}

private object BalanceManagementPreferencesKeys {
    val CURRENT_SIM_CARD_ID = stringPreferencesKey("CURRENT_SIM_CARD_ID")
}
