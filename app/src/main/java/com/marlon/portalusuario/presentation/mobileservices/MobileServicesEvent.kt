package com.marlon.portalusuario.presentation.mobileservices

sealed class MobileServicesEvent {
    data object OnUpdate : MobileServicesEvent()
    data object OnShowServiceSettings : MobileServicesEvent()
    data object OnHideServiceSettings : MobileServicesEvent()
    data object OnShowSImCardsSettings : MobileServicesEvent()
    data object OnHideSImCardsSettings : MobileServicesEvent()

    data class OnChangeCurrentMobileService(val value: String?) : MobileServicesEvent()
    data class OnSimCardPaired(val simId: String, val serviceId: String) : MobileServicesEvent()
}
