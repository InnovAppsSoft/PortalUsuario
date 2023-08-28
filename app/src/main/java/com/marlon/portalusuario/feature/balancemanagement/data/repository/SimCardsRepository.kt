package com.marlon.portalusuario.feature.balancemanagement.data.repository

import com.marlon.portalusuario.feature.balancemanagement.data.datasource.SimCardDataSource
import cu.suitetecsa.sdk.sim.model.SimCard
import javax.inject.Inject

class SimCardsRepository @Inject constructor(
    private val simCardDataSource: SimCardDataSource
) {
    fun getSimCards(): List<SimCard> = simCardDataSource.getSimCards()
    suspend fun getCurrentSimCard(): SimCard = simCardDataSource.getSimCards().first()
}