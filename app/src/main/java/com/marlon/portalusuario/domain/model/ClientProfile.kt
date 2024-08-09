package com.marlon.portalusuario.domain.model

data class ClientProfile(
    val email: String,
    val name: String,
    val mailNotifications: Boolean,
    val mobileNotifications: Boolean,
    val phoneNumber: String,
    val portalUser: String,
    val lastUpdate: String,
)
