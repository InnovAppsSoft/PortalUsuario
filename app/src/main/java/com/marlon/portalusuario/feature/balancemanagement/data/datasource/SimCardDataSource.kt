package com.marlon.portalusuario.feature.balancemanagement.data.datasource

import cu.suitetecsa.sdk.sim.model.SimCard

interface SimCardDataSource {
    fun getSimCards(): List<SimCard>
}