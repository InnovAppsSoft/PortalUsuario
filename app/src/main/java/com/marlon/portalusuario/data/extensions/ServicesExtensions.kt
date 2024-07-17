package com.marlon.portalusuario.data.extensions

import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import io.github.suitetecsa.sdk.nauta.model.NavService
import io.github.suitetecsa.sdk.nauta.model.MobileService as MobService
import io.github.suitetecsa.sdk.nauta.model.MobilePlan as MobPlan
import io.github.suitetecsa.sdk.nauta.model.MobileBonus as MobBonus

fun MobService.asModel() = MobileService(
    id = this.profile.id,
    lte = this.profile.lte == "true",
    advanceBalance = this.profile.advanceBalance,
    status = this.profile.status,
    lockDate = this.profile.lockDate,
    deletionDate = this.profile.deletionDate,
    saleDate = this.profile.saleDate,
    internet = this.profile.internet == "true",
    plans = this.profile.lists.plans.map { it.asModel() },
    bonuses = this.profile.lists.bonuses.map { it.asModel() },
    currency = this.profile.currency,
    phoneNumber = this.profile.phoneNumber,
    mainBalance = this.profile.mainBalance,
    consumptionRate = this.profile.consumptionRate == "true"
)

fun MobPlan.asModel() = MobilePlan(
    data = this.data,
    type = this.type,
    expires = this.expires
)

fun MobBonus.asModel() = MobileBonus(
    data = this.data,
    startDate = this.startDate,
    type = this.type,
    expires = this.expires
)

fun NavService.asModel() = NavigationService(
    id = this.profile.id,
    bonusToEnjoy = this.profile.bonusToEnjoy,
    accessAccount = this.profile.accessAccount,
    status = this.profile.status,
    lockDate = this.profile.lockDate,
    deletionDate = this.profile.deletionDate,
    saleDate = this.profile.saleDate,
    bonusHours = this.profile.bonusHours,
    currency = this.profile.currency,
    balance = this.profile.balance,
    accessType = this.profile.accessType,
    productType = this.productType
)