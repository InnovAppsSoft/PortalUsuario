package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.MobileService as Entity
import com.marlon.portalusuario.domain.model.MobileService as Domain

class MobServiceDomainToEntityMapper : Mapper<Domain, Entity> {
    override fun map(from: Domain) = Entity(
        id = from.id,
        lte = from.lte,
        advanceBalance = from.advanceBalance,
        status = from.status,
        lockDate = from.lockDate,
        deletionDate = from.deletionDate,
        saleDate = from.saleDate,
        internet = from.internet,
        plans = from.plans,
        bonuses = from.bonuses,
        currency = from.currency,
        phoneNumber = from.phoneNumber,
        mainBalance = from.mainBalance,
        consumptionRate = from.consumptionRate,
        slotIndex = from.slotIndex,
        type = from.type
    )
}
