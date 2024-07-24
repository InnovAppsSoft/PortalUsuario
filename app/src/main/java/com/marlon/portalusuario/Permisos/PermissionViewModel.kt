package com.marlon.portalusuario.Permisos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    var currentStep by mutableIntStateOf(0)
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
