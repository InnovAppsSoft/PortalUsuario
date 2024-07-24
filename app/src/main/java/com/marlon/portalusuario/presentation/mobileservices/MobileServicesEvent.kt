package com.marlon.portalusuario.presentation.mobileservices

sealed class MobileServicesEvent {
    data object OnUpdate : MobileServicesEvent()
    data class OnChangeCurrentMobileService(val value: String?) : MobileServicesEvent()
}
