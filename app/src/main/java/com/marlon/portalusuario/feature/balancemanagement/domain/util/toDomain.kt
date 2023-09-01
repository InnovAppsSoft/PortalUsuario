package com.marlon.portalusuario.feature.balancemanagement.domain.util

import com.marlon.portalusuario.feature.balancemanagement.domain.model.BonusBalanceDomainEntity
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.model.BonusBalance
import cu.suitetecsa.sdk.ussd.model.MainBalance

fun MainBalance.toDomain(simCardId: String) = MainBalanceDomainEntity(
    id = simCardId,
    credit = credit,
    data = data,
    voice = voice,
    sms = sms,
    daily = dailyData,
    mail = mailData,
    activeUntil = activeUntil,
    dueDate = mainBalanceDueDate
)


fun BonusBalance.toDomain(id: String): BonusBalanceDomainEntity =
    BonusBalanceDomainEntity(
        id = id,
        credit = credit,
        unlimitedData = unlimitedData,
        data = data,
        dataCu = dataCu
    )
