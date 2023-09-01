package com.marlon.portalusuario.feature.balancemanagement.domain.util

import cu.suitetecsa.sdk.ussd.model.BonusBalance
import cu.suitetecsa.sdk.ussd.model.BonusCredit
import cu.suitetecsa.sdk.ussd.model.BonusData
import cu.suitetecsa.sdk.ussd.model.BonusDataCU
import cu.suitetecsa.sdk.ussd.model.BonusUnlimitedData

internal val initialBonusBalance = BonusBalance(
    credit = BonusCredit(null, null),
    unlimitedData = BonusUnlimitedData(null),
    data = BonusData(null, null, null),
    dataCu = BonusDataCU(null, null)
)
