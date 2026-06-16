package com.marlon.portalusuario.feature.telephony.domain.usecases

class ValidatePhoneNumberUseCase {
    operator fun invoke(phoneNumber: String): Boolean = phoneNumber.length >= 8 && phoneNumber.all { it.isDigit() }
}
