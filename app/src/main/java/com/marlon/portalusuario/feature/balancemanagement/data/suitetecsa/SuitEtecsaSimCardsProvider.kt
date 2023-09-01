package com.marlon.portalusuario.feature.balancemanagement.data.suitetecsa

import com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource.SimCardDataSource
import cu.suitetecsa.sdk.sim.SimCardsAPI
import cu.suitetecsa.sdk.sim.model.SimCard
import javax.inject.Inject
import javax.inject.Singleton

class SuitEtecsaSimCardsProvider @Inject constructor(
    private val simCardsAPI: SimCardsAPI
) : SimCardDataSource {
    override fun getSimCards(): List<SimCard> = simCardsAPI.getSimCards()
}