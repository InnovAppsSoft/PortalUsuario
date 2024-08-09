package com.marlon.portalusuario.presentation.mobileservices.components.servsettings

import com.marlon.portalusuario.domain.model.MobileService
import io.github.suitetecsa.sdk.android.model.SimCard

sealed class ServiceSettingsEvent {
    data class OnTurnConsumptionRate(val simCard: SimCard?, val service: MobileService) : ServiceSettingsEvent()
}
