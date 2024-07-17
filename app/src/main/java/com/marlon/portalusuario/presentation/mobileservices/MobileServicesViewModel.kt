package com.marlon.portalusuario.presentation.mobileservices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MobileServicesViewModel @Inject constructor(
    private val preference: AppPreferences
) : ViewModel() {
    private var _state = MutableStateFlow(MobileServicesState())
    val state = _state.asStateFlow()

    fun onEvent(event: MobileServicesEvent) {
        when (event) {
            MobileServicesEvent.OnUpdate -> TODO()
            is MobileServicesEvent.OnChangeCurrentMobileService -> viewModelScope.launch {
                // preference.updateCurrentMobileService(event.currentMobileService)
            }
        }
    }
}