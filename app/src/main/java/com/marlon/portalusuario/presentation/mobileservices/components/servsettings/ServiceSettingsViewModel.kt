package com.marlon.portalusuario.presentation.mobileservices.components.servsettings

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.SimPaired
import com.marlon.portalusuario.presentation.mobileservices.usecases.UssdExecute
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.marlon.portalusuario.domain.model.AppPreferences as ModelAppPreferences

@HiltViewModel
class ServiceSettingsViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    simCardCollector: SimCardCollector,
    private val ussdExecute: UssdExecute
) : ViewModel() {
    val simCards = simCardCollector.collect()
    val preferences = appPreferences.preferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ModelAppPreferences()
    )
    private val _state = mutableStateOf(ServiceSettingsState())
    val state: State<ServiceSettingsState> get() = _state

    @SuppressLint("MissingPermission")
    fun onEvent(event: ServiceSettingsEvent) {
        when (event) {
            is ServiceSettingsEvent.OnPairSimCard -> updateSimsPaired(event.simCard, event.service)
            is ServiceSettingsEvent.OnTurnConsumptionRate -> turnConsumptionRate(event.simCard, event.service)
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

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun updateSimsPaired(simCard: SimCard?, service: MobileService) {
        viewModelScope.launch {
            if (service.id in preferences.value.simsPaired.map { it.serviceId }) {
                appPreferences.updateIsSimCardsPaired(
                    preferences.value.simsPaired.filter { it.serviceId != service.id }
                )
            }
            simCard?.let { sim ->
                if (sim.telephony.subscriberId in preferences.value.simsPaired.map { it.simId }) {
                    appPreferences.updateIsSimCardsPaired(
                        preferences.value.simsPaired.filter { it.simId != sim.telephony.subscriberId }
                    )
                }

                val newList = mutableListOf<SimPaired>()
                newList.addAll(preferences.value.simsPaired)
                newList.add(SimPaired(sim.telephony.subscriberId, service.id))

                appPreferences.updateIsSimCardsPaired(newList)
            }
        }
    }
}
