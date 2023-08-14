package com.marlon.portalusuario.nauta.ui.components.captchaview

import android.graphics.Bitmap

sealed class CaptchaViewState {
    object Loading : CaptchaViewState()
    data class Failure(val reason: String) : CaptchaViewState()
    data class Success(val image: Bitmap) : CaptchaViewState()
}