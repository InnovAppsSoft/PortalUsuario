package com.marlon.portalusuario.presentation.mobileservices

sealed class MobileServicesEvent {
    data class OnChangeCurrentMobileService(val currentMobileService: String) : MobileServicesEvent()
    data object OnUpdate : MobileServicesEvent()
}
