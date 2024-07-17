package com.marlon.portalusuario.presentation.resetpassword

sealed class ResetPasswordEvent {
    data object OnLoadCaptcha : ResetPasswordEvent()
    data object OnValidateUser : ResetPasswordEvent()
    data object OnValidateConfirmCode : ResetPasswordEvent()
    data object OnResetPassword : ResetPasswordEvent()
    data object OnTogglePasswordVisibility : ResetPasswordEvent()

    data class OnChangeUser(val value: String) : ResetPasswordEvent()
    data class OnChangeCaptchaCode(val value: String) : ResetPasswordEvent()
    data class OnChangeConfirmCode(val value: String) : ResetPasswordEvent()
    data class OnChangeNewPassword(val value: String) : ResetPasswordEvent()
    data class OnChangeConfirmPassword(val value: String) : ResetPasswordEvent()
}
