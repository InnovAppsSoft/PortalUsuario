package com.marlon.portalusuario.permisos

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel
    @Inject
    constructor() : ViewModel() {
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
