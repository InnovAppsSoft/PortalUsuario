package com.marlon.portalusuario.domain.model

import com.marlon.portalusuario.data.entity.NavigationService as EntityNavigationService

data class NavigationService(
    val id: String,
    val bonusToEnjoy: String,
    val accessAccount: String,
    val status: String,
    val lockDate: String,
    val deletionDate: String,
    val saleDate: String,
    val bonusHours: String,
    val currency: String,
    val balance: String,
    val accessType: String,
    val productType: String,
)

fun NavigationService.asEntity() = EntityNavigationService(
    id,
    bonusToEnjoy,
    accessAccount,
    status,
    lockDate,
    deletionDate,
    saleDate,
    bonusHours,
    currency,
    balance,
    accessType,
    productType
)
