package com.marlon.portalusuario.feature.balancemanagement.domain.model

import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice

data class MainBalanceDomainEntity(
    val id: String,
    val credit: Float?,
    val data: MainData,
    val voice: MainVoice,
    val sms: MainSms,
    val daily: DailyData,
    val mail: MailData,
    val activeUntil: String?,
    val dueDate: String?
)
