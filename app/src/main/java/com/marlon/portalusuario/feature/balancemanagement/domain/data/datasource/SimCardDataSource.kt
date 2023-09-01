package com.marlon.portalusuario.feature.balancemanagement.domain.data.datasource

import cu.suitetecsa.sdk.sim.model.SimCard

interface SimCardDataSource {
    fun getSimCards(): List<SimCard>
}