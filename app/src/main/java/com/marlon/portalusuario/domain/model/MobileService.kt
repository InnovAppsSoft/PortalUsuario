package com.marlon.portalusuario.domain.model

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
    val slotIndex: Int,
    val type: ServiceType,
    val lastUpdated: Long = 0,
    val subscriptionId: Int? = null,
)
