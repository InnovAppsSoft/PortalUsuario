package com.marlon.portalusuario.presentation.mobileservices

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.MobServPreferences
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.util.Utils.isAtLeastOneHourElapsed
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import io.github.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MobileServicesViewModel"

@HiltViewModel
class MobileServicesViewModel @Inject constructor(
    private val mobServicesPreferences: MobServicesPreferences,
    private val repository: UserRepository,
    private val simCardCollector: SimCardCollector
) : ViewModel() {
    val mobileServices = repository.getMobileServices().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )
    val preferences = mobServicesPreferences.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MobServPreferences(emptyList())
    )

    @SuppressLint("MissingPermission")
    val simCards = simCardCollector.collect()

    private var _state = mutableStateOf(MobileServicesState())
    val state: State<MobileServicesState> get() = _state

    init {
        viewModelScope.launch {
            val mobPreferences = mobServicesPreferences.preferences.first()
            _state.value = _state.value.copy(currentServiceId = mobPreferences.mssId)

            when {
                mobPreferences.slotIndexInfoList.isEmpty() && simCards.isNotEmpty() ->
                    onEvent(MobileServicesEvent.OnShowSImCardsSettings)
                mobileServices.value.isNotEmpty() -> {
                    mobServicesPreferences.updateSlotIndexInfoList(
                        mobPreferences.slotIndexInfoList
                            .filter { it.index in simCards.map { sim -> sim.slotIndex } }
                    )
                    if (mobileServices.value.first { it.id == _state.value.currentServiceId }.lastUpdated.isAtLeastOneHourElapsed()) update()
                }
                simCards.isNotEmpty() -> {
                    mobServicesPreferences.updateSlotIndexInfoList(
                        mobPreferences.slotIndexInfoList
                            .filter { it.index in simCards.map { sim -> sim.slotIndex } }
                    )
                    if (mobileServices.value.first { it.id == _state.value.currentServiceId }.lastUpdated.isAtLeastOneHourElapsed()) update()
                }
            }
        }
    }

    fun onEvent(event: MobileServicesEvent) {
        when (event) {
            MobileServicesEvent.OnUpdate -> update()
            is MobileServicesEvent.OnChangeCurrentMobileService -> viewModelScope.launch {
                _state.value = _state.value.copy(currentServiceId = event.value)
                mobServicesPreferences.updateMobileServiceSelectedId(event.value)
                Log.d(TAG, "onEvent: changed serviceId: ${event.value}")
            }

            MobileServicesEvent.OnHideServiceSettings ->
                _state.value = _state.value.copy(isServiceSettingsVisible = false)

            MobileServicesEvent.OnShowServiceSettings ->
                _state.value = _state.value.copy(isServiceSettingsVisible = true)

            MobileServicesEvent.OnHideSImCardsSettings -> {
                _state.value = _state.value.copy(isSimCardsSettingsVisible = false)
            }
            MobileServicesEvent.OnShowSImCardsSettings ->
                _state.value = _state.value.copy(isSimCardsSettingsVisible = true)

            MobileServicesEvent.OnErrorDismiss ->
                _state.value = _state.value.copy(error = null)
        }
    }

    private val currentService: MobileService
        get() = mobileServices.value.first { it.id == _state.value.currentServiceId }

    private val simCardForFetch: SimCard?
        get() = simCards.firstOrNull { it.slotIndex == currentService.slotIndex }
            ?.copy(phoneNumber = currentService.phoneNumber)

    private fun update(onlyRemote: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { repository.fetchUser(if (onlyRemote) null else simCardForFetch) }
                .onFailure { _state.value = _state.value.copy(error = it.message) }
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
