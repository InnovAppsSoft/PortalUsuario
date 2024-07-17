package com.marlon.portalusuario.domain.model

import com.marlon.portalusuario.data.entity.MobileService as EntityMobileService

data class MobileService(
    val id: String,
    val lte: Boolean,
    val advanceBalance: String,
    val status: String,
    val lockDate: String,
    val deletionDate: String,
    val saleDate: String,
    val internet: Boolean,
    val plans: List<MobilePlan>,
    val bonuses: List<MobileBonus>,
    val currency: String,
    val phoneNumber: String,
    val mainBalance: String,
    val consumptionRate: Boolean,
)

fun MobileService.asEntity() = EntityMobileService(
    id,
    lte,
    advanceBalance,
    status,
    lockDate,
    deletionDate,
    saleDate,
    internet,
    plans,
    bonuses,
    currency,
    phoneNumber,
    mainBalance,
    consumptionRate
)
