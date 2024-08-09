package com.marlon.portalusuario.data.extensions

import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import io.github.suitetecsa.sdk.nauta.model.MobileBonus as MobBonus
import io.github.suitetecsa.sdk.nauta.model.MobilePlan as MobPlan

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
