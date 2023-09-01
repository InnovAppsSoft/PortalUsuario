package com.marlon.portalusuario.feature.balancemanagement.data.room.util

import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.BonusBalanceCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.DailyDataCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MailDataCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainBalanceCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainDataCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainSmsCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainVoiceCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.domain.model.BonusBalanceDomainEntity
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity
import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice

interface EntityMapper<Cache, Domain> {
    fun mapToDomain(cache: Cache): Domain
    fun mapFromDomain(domain: Domain): Cache
}
