package com.marlon.portalusuario.nauta.domain.model

import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.nauta.data.entities.User

data class UserModel(
    val id: Int,
    val username: String,
    val password: String,
    var remainingTime: Int,
    var credit: Float,
    val email: String,
    val serviceType: NavigationType,
    val accountType: String,
    val blockingDate: String,
    val dateOfElimination: String,
    val offer: String? = null,
    val phone: String? = null,
    val uploadSpeeds: String? = null,
    val downloadSpeeds: String? = null,
    val linkIdentifiers: String? = null,
    val linkStatus: String? = null,
    val activationDate: String? = null,
    val blockingDateHome: String? = null,
    val dateOfEliminationHome: String? = null,
    val monthlyFee: String? = null,
    val quotePaid: String? = null,
    val voucher: String? = null,
    val debt: String? = null
)