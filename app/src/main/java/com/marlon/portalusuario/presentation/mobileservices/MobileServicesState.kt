package com.marlon.portalusuario.presentation.mobileservices

import com.marlon.portalusuario.domain.model.MobileService

data class MobileServicesState(
    val isLoading: Boolean = false,
    val currentService: MobileService? = null
)
