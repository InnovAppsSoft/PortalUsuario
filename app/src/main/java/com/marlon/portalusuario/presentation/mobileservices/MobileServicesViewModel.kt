package com.marlon.portalusuario.presentation.mobileservices

import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.data.preferences.MobServicesPreferences
import com.marlon.portalusuario.data.preferences.SessionStorage
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.DataSession
import com.marlon.portalusuario.domain.model.SimPaired
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.android.SimCardCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.marlon.portalusuario.domain.model.AppPreferences as ModelAppPreferences

private const val TAG = "MobileServicesViewModel"

@HiltViewModel
class MobileServicesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val mobServicesPreferences: MobServicesPreferences,
    private val repository: UserRepository,
    sessionStorage: SessionStorage,
    simCardCollector: SimCardCollector
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
    val session: StateFlow<DataSession?> = sessionStorage.dataSession.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    val simCards = simCardCollector.collect()

    private var _state = mutableStateOf(MobileServicesState())
    val state: State<MobileServicesState> get() = _state

    init {
        viewModelScope.launch {
            val slotIndexInfoList = mobServicesPreferences.preferences.first().slotIndexInfoList

            when {
                slotIndexInfoList.isEmpty() && simCards.isNotEmpty() ->
                    onEvent(MobileServicesEvent.OnShowSImCardsSettings)
                mobileServices.value.isNotEmpty() -> {
                    mobServicesPreferences.updateSlotIndexInfoList(
                        slotIndexInfoList.filter { it.index in simCards.map { sim -> sim.slotIndex } }
                    )
                    update()
                }
                simCards.isNotEmpty() -> {
                    mobServicesPreferences.updateSlotIndexInfoList(
                        slotIndexInfoList.filter { it.index in simCards.map { sim -> sim.slotIndex } }
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { update() }
                }
            }
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

            MobileServicesEvent.OnHideSImCardsSettings -> {
                _state.value = _state.value.copy(isSimCardsSettingsVisible = false)
                update(Build.VERSION.SDK_INT < Build.VERSION_CODES.O || session.value != null)
            }
            MobileServicesEvent.OnShowSImCardsSettings ->
                _state.value = _state.value.copy(isSimCardsSettingsVisible = true)
        }
    }

    private fun update(onlyRemote: Boolean = false) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.fetchUser(
                if (onlyRemote) {
                    null
                } else {
                    simCards.firstOrNull { sim ->
                        sim.slotIndex == mobileServices.value
                            .first { it.id == _state.value.currentServiceId }.slotIndex
                    }
                }
            )
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
