package com.marlon.portalusuario.nauta.core

import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.nauta.domain.model.UserModel

val INITIAL_USER = UserModel(
    id = 0,
    username = "",
    password = "",
    remainingTime = 0,
    credit = 0.0f,
    email = "",
    serviceType = NavigationType.NATIONAL,
    accountType = "",
    blockingDate = "",
    dateOfElimination = "",
    offer = null,
    phone = null,
    uploadSpeeds = null,
    downloadSpeeds = null,
    linkIdentifiers = null,
    linkStatus = null,
    activationDate = null,
    blockingDateHome = null,
    dateOfEliminationHome = null,
    monthlyFee = null,
    quotePaid = null,
    voucher = null,
    debt = null
)