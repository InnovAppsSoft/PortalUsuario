package com.marlon.portalusuario.presentation.auth

sealed class AuthEvent {
    data class OnChangePhoneNumber(val value: String) : AuthEvent()
    data class OnChangePassword(val value: String) : AuthEvent()
    data object OnTogglePasswordVisibility : AuthEvent()
    data object OnLoadCaptcha : AuthEvent()
    data class OnChangeCaptchaCode(val value: String) : AuthEvent()
    data object OnAuth : AuthEvent()
}
