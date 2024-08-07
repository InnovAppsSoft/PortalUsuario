package com.marlon.portalusuario.presentation.signup

sealed class SignupEvent {
    data object OnLoadCaptcha : SignupEvent()
    data object OnRegisterUser : SignupEvent()
    data object OnConfirmCode : SignupEvent()
    data object OnCreateUser : SignupEvent()
    data object OnTogglePasswordVisibility : SignupEvent()
    data object OnErrorDismiss : SignupEvent()

    data class OnChangedPhoneNumber(val value: String) : SignupEvent()
    data class OnChangedDNI(val value: String) : SignupEvent()
    data class OnChangedCaptchaCode(val value: String) : SignupEvent()
    data class OnChangedConfirmCode(val value: String) : SignupEvent()
    data class OnChangedPassword(val value: String) : SignupEvent()
    data class OnChangedConfirmPassword(val value: String) : SignupEvent()
}
