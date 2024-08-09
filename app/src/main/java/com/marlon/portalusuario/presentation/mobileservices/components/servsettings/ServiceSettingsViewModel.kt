package com.marlon.portalusuario.presentation.mobileservices.components.servsettings

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.presentation.mobileservices.usecases.UssdExecute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.model.SimCard
import javax.inject.Inject

@HiltViewModel
class ServiceSettingsViewModel @Inject constructor(
    simCardCollector: SimCardCollector,
    private val ussdExecute: UssdExecute
) : ViewModel() {
    val simCards = simCardCollector.collect()
    private val _state = mutableStateOf(ServiceSettingsState())
    val state: State<ServiceSettingsState> get() = _state

    @SuppressLint("MissingPermission")
    fun onEvent(event: ServiceSettingsEvent) {
        when (event) {
            is ServiceSettingsEvent.OnTurnConsumptionRate ->
                turnConsumptionRate(event.simCard, event.service)
        }
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun turnConsumptionRate(simCard: SimCard?, service: MobileService) {
        val ussdCode = if (service.consumptionRate) {
            "*133*1*1*1${Uri.parse("#")}"
        } else {
            "*133*1*1*2${Uri.parse("#")}"
        }
        simCard?.let { ussdExecute(it, ussdCode) }
    }
}
