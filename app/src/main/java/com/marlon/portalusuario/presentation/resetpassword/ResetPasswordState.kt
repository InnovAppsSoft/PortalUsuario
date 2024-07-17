package com.marlon.portalusuario.presentation.resetpassword

import android.graphics.Bitmap

data class ResetPasswordState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoadingCaptcha: Boolean = false,
    val captchaImage: Bitmap? = null,
    val idRequest: String = "",
    val phoneNumber: String = "",
    val captchaCode: String = "",
    val confirmCode: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false
)
