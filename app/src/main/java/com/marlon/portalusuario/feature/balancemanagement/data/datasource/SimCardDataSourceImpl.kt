package com.marlon.portalusuario.feature.balancemanagement.data.datasource

import cu.suitetecsa.sdk.sim.SimCardsAPI
import cu.suitetecsa.sdk.sim.model.SimCard
import javax.inject.Inject

class SimCardDataSourceImpl @Inject constructor(
    private val simCardsAPI: SimCardsAPI
) : SimCardDataSource {
    override fun getSimCards(): List<SimCard> = simCardsAPI.getSimCards()
}