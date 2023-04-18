package com.marlon.portalusuario.commons

import com.marlon.portalusuario.nauta.data.entities.User
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser

fun NautaUser.toLocalUser(user: User): User {
    return User(
        id = user.id,
        userName = user.userName,
        password = user.password,
        accountNavigationType = user.accountNavigationType,
        lastConnection = user.lastConnection,
        blockingDate = this.blockingDate,
        dateOfElimination = this.dateOfElimination,
        accountType = this.accountType,
        serviceType = this.serviceType,
        credit = this.credit,
        time = this.time,
        mailAccount = this.mailAccount,
        offer = this.offer,
        monthlyFee = this.monthlyFee,
        downloadSpeed = this.downloadSpeed,
        uploadSpeed = this.uploadSpeed,
        phone = this.phone,
        linkIdentifiers = this.linkIdentifiers,
        linkStatus = this.linkStatus,
        activationDate = this.activationDate,
        blockingDateHome = this.blockingDateHome,
        dateOfEliminationHome = this.dateOfEliminationHome,
        quotePaid = this.quotePaid,
        voucher = this.voucher,
        debt = this.debt
    )
}

fun User.fullUserName(): String =
    "${this.userName}@nauta.c${if (this.accountNavigationType == NavigationType.INTERNATIONAL) "om" else "o"}.cu"