package com.marlon.portalusuario.feature.balancemanagement.domain.util

import cu.suitetecsa.sdk.ussd.model.DailyData
import cu.suitetecsa.sdk.ussd.model.MailData
import cu.suitetecsa.sdk.ussd.model.MainBalance
import cu.suitetecsa.sdk.ussd.model.MainData
import cu.suitetecsa.sdk.ussd.model.MainSms
import cu.suitetecsa.sdk.ussd.model.MainVoice

internal val initialMainBalance = MainBalance(
    null,
    MainData(false ,null, null, null),
    MainVoice(null, null),
    MainSms(null, null),
    DailyData(null, null),
    MailData(null, null),
    null,
    null
)