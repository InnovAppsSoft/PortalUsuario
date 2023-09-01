package com.marlon.portalusuario.feature.balancemanagement.domain.model

import cu.suitetecsa.sdk.ussd.model.BonusCredit
import cu.suitetecsa.sdk.ussd.model.BonusData
import cu.suitetecsa.sdk.ussd.model.BonusDataCU
import cu.suitetecsa.sdk.ussd.model.BonusUnlimitedData

data class BonusBalanceDomainEntity(
    val id: String,
    val credit: BonusCredit,
    val unlimitedData: BonusUnlimitedData,
    val data: BonusData,
    val dataCu: BonusDataCU
)
