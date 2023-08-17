package com.marlon.portalusuario.nauta.ui.components.captchadialog

import android.graphics.Bitmap

sealed class CaptchaDialogState {
    data class Showing(
        val isExecutable: Boolean = false,
        val isActionExecuting: Boolean = false
    ) : CaptchaDialogState()

    object Hidden : CaptchaDialogState()
}