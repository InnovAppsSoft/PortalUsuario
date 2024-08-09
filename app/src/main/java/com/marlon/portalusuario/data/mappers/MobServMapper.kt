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
            profile.id,
            profile.lte == "true",
            profile.advanceBalance,
            profile.status,
            profile.lockDate,
            profile.deletionDate,
            profile.saleDate,
            profile.internet == "true",
            profile.lists.plans.map { it.asModel() },
            profile.lists.bonuses.map { it.asModel() },
            profile.currency,
            profile.phoneNumber,
            profile.mainBalance,
            profile.consumptionRate == "true",
            slotIndex?.index ?: -1,
            slotIndex?.let { ServiceType.LocalAndRemote } ?: ServiceType.Remote
        )
    }

    override fun MobEntity.toDomain() = MobDomain(
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
        consumptionRate,
        slotIndex,
        type
    )
}
