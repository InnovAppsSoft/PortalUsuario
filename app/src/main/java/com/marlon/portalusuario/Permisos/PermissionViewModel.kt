package com.marlon.portalusuario.Permisos

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    var currentStep by mutableStateOf(0)
        private set

    fun nextStep() {
        currentStep++
    }

    fun onOverlayPermissionResult(canDrawOverlays: Boolean) {
        if (canDrawOverlays) {
            nextStep()
        }
    }
}

