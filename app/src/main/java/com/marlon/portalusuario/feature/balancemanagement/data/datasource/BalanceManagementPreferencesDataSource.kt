package com.marlon.portalusuario.feature.balancemanagement.data.datasource

import com.marlon.portalusuario.feature.balancemanagement.data.model.BalancePreferences
import kotlinx.coroutines.flow.Flow

interface BalanceManagementPreferencesDataSource {
    fun balancePreferences(): Flow<BalancePreferences>
    suspend fun updateCurrentSimCardId(simCardId: String)
}

