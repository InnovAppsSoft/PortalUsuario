package com.marlon.portalusuario.feature.mobileservices.presentation

data class MobileServicesState(
    val isLoading: Boolean = false,
    val currentServiceId: String? = null,
    val isServiceSettingsVisible: Boolean = false,
    val isSimCardsSettingsVisible: Boolean = false,
    val error: String? = null,
)
