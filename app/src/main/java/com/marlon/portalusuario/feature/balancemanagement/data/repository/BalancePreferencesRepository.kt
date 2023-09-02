package com.marlon.portalusuario.feature.balancemanagement.data.repository

import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.BalanceManagementPreferencesDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalancePreferencesRepository @Inject constructor(
    private val balancePreferencesDataSource: BalanceManagementPreferencesDataSource
) {
    fun getBalancePreferences() = balancePreferencesDataSource.balancePreferences()
    suspend fun updateCurrentSimCardId(simCardId: String) =
        balancePreferencesDataSource.updateCurrentSimCardId(simCardId)
}