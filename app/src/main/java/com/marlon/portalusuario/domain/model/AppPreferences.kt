package com.marlon.portalusuario.domain.model

data class AppPreferences(
    val mssId: String? = null, // MobileServiceSelectedId
    val simsPaired: List<SimPaired> = emptyList(),
    val skippedLogin: Boolean = false,
)
