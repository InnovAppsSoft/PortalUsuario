package com.marlon.portalusuario.data.mappers

import com.marlon.portalusuario.data.entity.NavigationService as NavEntity
import com.marlon.portalusuario.domain.model.NavigationService as NavDomain
import io.github.suitetecsa.sdk.nauta.model.NavService as NavApi

class NavServMapper : Mapper<NavDomain, NavEntity, NavApi> {

    override fun NavApi.toEntity() = NavEntity(
        profile.id,
        profile.bonusToEnjoy,
        profile.accessAccount,
        profile.status,
        profile.lockDate,
        profile.deletionDate,
        profile.saleDate,
        profile.bonusHours,
        profile.currency,
        profile.balance,
        profile.accessType,
        productType
    )

    override fun NavEntity.toDomain() = NavDomain(
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
}
