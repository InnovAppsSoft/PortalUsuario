package com.marlon.portalusuario.feature.balancemanagement.data.room.util

import javax.inject.Inject
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.BonusBalanceCacheEntity as CacheEntity
import com.marlon.portalusuario.feature.balancemanagement.domain.model.BonusBalanceDomainEntity as domainEntity

class BonusBalanceCacheMapper @Inject constructor() : EntityMapper<CacheEntity, domainEntity> {
    override fun mapToDomain(cache: CacheEntity): domainEntity =
        domainEntity(
            cache.id,
            cache.credit,
            cache.unlimitedData,
            cache.data,
            cache.dataCu
        )
    override fun mapFromDomain(domain: domainEntity): CacheEntity =
        CacheEntity(
            domain.id,
            domain.credit,
            domain.unlimitedData,
            domain.data,
            domain.dataCu
        )
}