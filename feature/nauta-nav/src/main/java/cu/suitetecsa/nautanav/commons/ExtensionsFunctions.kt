package cu.suitetecsa.nautanav.commons

import cu.suitetecsa.nautanav.core.toPriceFloat
import cu.suitetecsa.nautanav.core.toSeconds
import cu.suitetecsa.nautanav.data.entities.User
import cu.suitetecsa.nautanav.util.NavigationType
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser

fun NautaUser.toLocalUser(user: User): User {
    return User(
        id = user.id,
        userName = user.userName,
        password = user.password,
        blockingDate = this.blockingDate,
        dateOfElimination = this.dateOfElimination,
        accountType = this.accountType,
        serviceType =
        if (this.serviceType.contains("Navegaci\u00f3n Internacional")) NavigationType.INTERNATIONAL
        else NavigationType.NATIONAL,
        credit = this.credit.toPriceFloat(),
        remainingTime = this.time.toSeconds(),
        email = this.mailAccount,
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