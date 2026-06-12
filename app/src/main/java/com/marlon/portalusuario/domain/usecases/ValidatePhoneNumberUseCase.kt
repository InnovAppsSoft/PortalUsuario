package com.marlon.portalusuario.domain.usecases

class ValidatePhoneNumberUseCase {
    operator fun invoke(phoneNumber: String): Boolean {
        return phoneNumber.length >= 8 && phoneNumber.all { it.isDigit() }
    }
}
