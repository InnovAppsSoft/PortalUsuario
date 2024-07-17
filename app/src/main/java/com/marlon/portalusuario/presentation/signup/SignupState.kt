package com.marlon.portalusuario.presentation.signup

import android.graphics.Bitmap

data class SignupState(
    val isLoading: Boolean = false,
    val phoneNumber: String = "",
    val dni: String = "",
    val captchaCode: String = "",
    val captchaImage: Bitmap? = null,
    val idRequest: String = "",
    val isLoadingCaptcha: Boolean = false,
    val error: String? = null,
    val confirmCode: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
)
