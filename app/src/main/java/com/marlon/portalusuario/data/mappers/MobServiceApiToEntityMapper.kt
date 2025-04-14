package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.domain.model.ServiceType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import io.github.suitetecsa.sdk.nauta.model.MobileService as MobApi
import com.marlon.portalusuario.data.entity.MobileService as MobEntity

class MobServiceApiToEntityMapper(
    private val preferences: MobServicesPreferences,
    private val mobPlanMapper: MobPlanApiToModelMapper,
    private val mobBonusMapper: MobBonusApiToModelMapper,
) : Mapper<MobApi, MobEntity> {
    override fun map(from: MobApi): MobEntity {
        val slotIndex = runBlocking { preferences.preferences.first().slotIndexInfoList }
            .firstOrNull { it.phoneNumber == from.profile.phoneNumber }

        return MobEntity(
            id = from.profile.id,
            lte = from.profile.lte == "true",
            advanceBalance = from.profile.advanceBalance,
            status = from.profile.status,
            lockDate = from.profile.lockDate,
            deletionDate = from.profile.deletionDate,
            saleDate = from.profile.saleDate,
            internet = from.profile.internet == "true",
            plans = from.profile.lists.plans.map(mobPlanMapper::map),
            bonuses = from.profile.lists.bonuses.map(mobBonusMapper::map),
            currency = from.profile.currency,
            phoneNumber = from.profile.phoneNumber,
            mainBalance = from.profile.mainBalance,
            consumptionRate = from.profile.consumptionRate == "true",
            slotIndex = slotIndex?.index ?: -1,
            type = slotIndex?.let { ServiceType.LocalAndRemote } ?: ServiceType.Remote
        )
    }
}
