package com.marlon.portalusuario.domain.model

import com.marlon.portalusuario.data.entity.ClientProfile as EntityClientProfile

data class ClientProfile(
    val email: String,
    val name: String,
    val mailNotifications: Boolean,
    val mobileNotifications: Boolean,
    val phoneNumber: String,
    val portalUser: String,
    val lastUpdate: String,
)

fun ClientProfile.asEntity() = EntityClientProfile(
    email = email,
    name = name,
    mailNotifications = mailNotifications,
    mobileNotifications = mobileNotifications,
    phoneNumber = phoneNumber,
    portalUser = portalUser,
    lastUpdate = lastUpdate
)
