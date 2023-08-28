package com.marlon.portalusuario.feature.balancemanagement.data.repository

import com.marlon.portalusuario.feature.balancemanagement.data.datasource.BalanceManagementPreferencesDataSource
import javax.inject.Inject

class BalancePreferencesRepository @Inject constructor(
    private val balancePreferencesDataSource: BalanceManagementPreferencesDataSource
) {
    fun getBalancePreferences() = balancePreferencesDataSource.balancePreferences()
    suspend fun updateCurrentSimCardId(simCardId: String) =
        balancePreferencesDataSource.updateCurrentSimCardId(simCardId)
}