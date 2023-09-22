package cu.suitetecsa.nautanav.core

import cu.suitetecsa.nautanav.util.NavigationType
import cu.suitetecsa.nautanav.data.entities.User
import cu.suitetecsa.nautanav.domain.model.UserModel
import cu.suitetecsa.sdk.nauta.domain.model.NautaUser
import cu.suitetecsa.sdk.nauta.domain.util.floatToPriceString
import cu.suitetecsa.sdk.nauta.domain.util.priceStringToFloat
import cu.suitetecsa.sdk.nauta.domain.util.secondsToTimeString
import cu.suitetecsa.sdk.nauta.domain.util.timeStringToSeconds

fun NautaUser.toEntity(id: Int = 0, password: String): User =
    User(
        id = id,
        userName = this.userName,
        password = password,
        blockingDate = this.blockingDate,
        dateOfElimination = this.dateOfElimination,
        accountType = this.accountType,
        serviceType =
        if (this.serviceType.contains("Navegaci\\u00f3n Internacional")) NavigationType.INTERNATIONAL
        else NavigationType.NATIONAL,
        credit = priceStringToFloat(this.credit),
        remainingTime = timeStringToSeconds(this.time),
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

fun UserModel.toData() = User(
    id = this.id,
    userName = this.username,
    password = this.password,
    remainingTime = this.remainingTime,
    credit = this.credit,
    email = this.email,
    serviceType = this.serviceType,
    accountType = this.accountType,
    blockingDate = this.blockingDate,
    dateOfElimination = this.dateOfElimination,
    offer = this.offer,
    phone = this.phone,
    uploadSpeed = this.uploadSpeeds,
    downloadSpeed = this.downloadSpeeds,
    linkIdentifiers = this.linkIdentifiers,
    linkStatus = this.linkStatus,
    activationDate = this.activationDate,
    blockingDateHome = this.blockingDateHome,
    dateOfEliminationHome = this.dateOfEliminationHome,
    monthlyFee = this.monthlyFee,
    quotePaid = this.quotePaid,
    voucher = this.voucher,
    debt = this.debt
)

fun Int.toTimeString(): String = secondsToTimeString(this)

fun String.toSeconds(): Int = timeStringToSeconds(this)

fun String.isValidUsername(): Boolean = this.endsWith("@nauta.com.cu") || this.endsWith("@nauta.co.cu")

fun String.isValidPassword(): Boolean = this.length in 8..16

fun String.isValidCaptchaCode(): Boolean = this.length in 4..10

fun String.isValidRechargeCode(): Boolean = this.replace(" ", "").length in listOf(12, 16)

fun String.isValidAmountToTransfer(user: UserModel): Boolean =
    this != "0" && this != "" && this.toFloat() <= user.credit

fun String.formatAsRechargeCode(): String {
    val formattedText = this.replace(" ", "")

    // crea una expesion regular para dividdir la cadena en grupos de cuatro caracteres
    val regex = "(.{4})".toRegex()

    // reeplaza cada grupo de cuatro caracteres con el mismo grupo seguido de un espacio
    return formattedText.replace(regex, "$1 ").trim()
}

fun String.toPriceFloat(): Float = priceStringToFloat(this)

fun Float.toPriceString(): String = floatToPriceString(this)