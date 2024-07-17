package com.marlon.portalusuario.presentation.auth

import android.graphics.Bitmap

data class AuthState(
    val phoneNumber: String = "",
    val password: String = "",
    val captchaCode: String = "",
    val captchaImage: Bitmap? = null,
    val idRequest: String? = null,
    val isLoading: Boolean = false,
    val isLoadingCaptcha: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val error: String? = null
)
