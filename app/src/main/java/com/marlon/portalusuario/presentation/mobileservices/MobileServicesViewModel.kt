package com.marlon.portalusuario.presentation.mobileservices

import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.SimPaired
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.utils.extractShortNumber
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.marlon.portalusuario.domain.model.AppPreferences as ModelAppPreferences

private const val TAG = "MobileServicesViewModel"

@HiltViewModel
class MobileServicesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val repository: UserRepository,
    simCardCollector: SimCardCollector,
) : ViewModel() {
    val mobileServices = repository.getMobileServices().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
    val preferences = appPreferences.preferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ModelAppPreferences()
    )
    val simCards = simCardCollector.collect()

    private var _state = mutableStateOf(MobileServicesState())
    val state: State<MobileServicesState> get() = _state

    val servicesPaired: List<SimPaired> get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mobileServices.value.filter {
                it.phoneNumber in simCards.map { sim -> extractShortNumber(sim.phoneNumber!!) }
            }.map { service ->
                SimPaired(
                    simId = simCards.first { extractShortNumber(it.phoneNumber!!) == service.phoneNumber }
                        .slotIndex.toString(),
                    serviceId = service.id
                )
            }
        } else {
            preferences.value.simsPaired
        }
    }

    fun onEvent(event: MobileServicesEvent) {
        when (event) {
            MobileServicesEvent.OnUpdate -> update()
            is MobileServicesEvent.OnChangeCurrentMobileService -> viewModelScope.launch {
                appPreferences.updateMobileServiceSelectedId(event.value)
                Log.d(TAG, "onEvent: changed serviceId: ${event.value}")
            }
            is MobileServicesEvent.OnSimCardPaired -> viewModelScope.launch {
                appPreferences.updateIsSimCardsPaired(
                    preferences.value.simsPaired.let {
                        val list = mutableListOf<SimPaired>()
                        list.addAll(it)
                        list.add(SimPaired(event.simId, event.serviceId))
                        list
                    }
                )
            }

            MobileServicesEvent.OnHideServiceSettings ->
                _state.value = _state.value.copy(isServiceSettingsVisible = false)

            MobileServicesEvent.OnShowServiceSettings ->
                _state.value = _state.value.copy(isServiceSettingsVisible = true)
        }
    }

    private fun update() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.fetchUser()
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
