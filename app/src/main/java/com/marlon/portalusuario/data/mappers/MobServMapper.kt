package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.extensions.asModel
import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.domain.model.ServiceType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.marlon.portalusuario.data.entity.MobileService as MobEntity
import com.marlon.portalusuario.domain.model.MobileService as MobDomain
import io.github.suitetecsa.sdk.nauta.model.MobileService as MobApi

class MobServMapper(private val preferences: MobServicesPreferences) : Mapper<MobDomain, MobEntity, MobApi> {
    override fun MobApi.toEntity(): MobEntity {
        val slotIndex = runBlocking { preferences.preferences.first().slotIndexInfoList }
            .firstOrNull { it.phoneNumber == profile.phoneNumber }

        return MobEntity(
            id = profile.id,
            lte = profile.lte == "true",
            advanceBalance = profile.advanceBalance,
            status = profile.status,
            lockDate = profile.lockDate,
            deletionDate = profile.deletionDate,
            saleDate = profile.saleDate,
            internet = profile.internet == "true",
            plans = profile.lists.plans.map { it.asModel() },
            bonuses = profile.lists.bonuses.map { it.asModel() },
            currency = profile.currency,
            phoneNumber = profile.phoneNumber,
            mainBalance = profile.mainBalance,
            consumptionRate = profile.consumptionRate == "true",
            slotIndex = slotIndex?.index ?: -1,
            type = slotIndex?.let { ServiceType.LocalAndRemote } ?: ServiceType.Remote
        )
    }

    override fun MobEntity.toDomain() = MobDomain(
        id = id,
        lte = lte,
        advanceBalance = advanceBalance,
        status = status,
        lockDate = lockDate,
        deletionDate = deletionDate,
        saleDate = saleDate,
        internet = internet,
        plans = plans,
        bonuses = bonuses,
        currency = currency,
        phoneNumber = phoneNumber,
        mainBalance = mainBalance,
        consumptionRate = consumptionRate,
        slotIndex = slotIndex,
        type = type,
        lastUpdated = lastUpdated
    )
}
