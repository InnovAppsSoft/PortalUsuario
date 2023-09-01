package com.marlon.portalusuario.feature.balancemanagement.data.room.util

import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.DailyDataCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MailDataCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainBalanceCacheEntity as CacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainDataCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainSmsCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.data.room.entity.MainVoiceCacheEntity
import com.marlon.portalusuario.feature.balancemanagement.domain.model.MainBalanceDomainEntity as DomainEntity
import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice
import javax.inject.Inject

class MainBalanceCacheMapper @Inject constructor(): EntityMapper<CacheEntity, DomainEntity> {
    override fun mapToDomain(cache: CacheEntity): DomainEntity =
        DomainEntity(
            cache.id,
            cache.credit,
            MainData(
                cache.data.usageBasedPricing,
                cache.data.mainDataCount,
                cache.data.mainDataLteCount,
                cache.data.mainDataRemainingDays
            ),
           MainVoice(
               cache.voice.mainVoice,
               cache.voice.mainVoiceRemainingDays
           ),
            MainSms(
                cache.sms.mainSms,
                cache.sms.mainSmsRemainingDays
            ),
            DailyData(
                cache.daily.dailyDataCount,
                cache.daily.dailyDataRemainingHours
            ),
            MailData(
                cache.mail.mailDataCount,
                cache.mail.mailDataRemainingDays
            ),
            cache.activeUntil,
            cache.dueDate
        )
    override fun mapFromDomain(domain: DomainEntity): CacheEntity =
        CacheEntity(
            domain.id,
            domain.credit,
            MainDataCacheEntity(
                domain.data.usageBasedPricing,
                domain.data.data,
                domain.data.dataLte,
                domain.data.remainingDays
            ),
            MainVoiceCacheEntity(
                domain.voice.mainVoice,
                domain.voice.remainingDays
            ),
            MainSmsCacheEntity(
                domain.sms.mainSms,
                domain.sms.remainingDays
            ),
            DailyDataCacheEntity(
                domain.daily.data,
                domain.daily.remainingHours
            ),
            MailDataCacheEntity(
                domain.mail.data,
                domain.mail.remainingDays
            ),
            domain.activeUntil,
            domain.dueDate
        )
}
