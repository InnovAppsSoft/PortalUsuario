package com.marlon.portalusuario.data.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.domain.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppPreferencesViewModel @Inject constructor(
    private val appPreferencesManager: AppPreferencesManager
) : ViewModel() {
    val state = appPreferencesManager.preferences().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AppSettings()
    )

    fun onEvent(event: AppPreferencesEvent) {
        viewModelScope.launch {
            when (event) {
                is AppPreferencesEvent.OnUpdateIsShowingTrafficBubble ->
                    appPreferencesManager.updateIsShowingTrafficBubble(event.value)
                is AppPreferencesEvent.OnUpdateModeNight ->
                    appPreferencesManager.updateModeNight(event.value)
                is AppPreferencesEvent.OnUpdateSkippedLogin -> {}
                is AppPreferencesEvent.OnUpdateIsIntroOpened ->
                    appPreferencesManager.updateIsIntroOpened(event.value)
            }
        }
    }
}
