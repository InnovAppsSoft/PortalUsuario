package com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource

import com.marlon.portalusuario.feature.balancemanagement.domain.model.BalancePreferences
import kotlinx.coroutines.flow.Flow

interface BalanceManagementPreferencesDataSource {
    fun balancePreferences(): Flow<BalancePreferences>
    suspend fun updateCurrentSimCardId(simCardId: String)
}

