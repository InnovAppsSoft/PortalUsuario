package com.marlon.portalusuario.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.data.preferences.SessionStorage
import com.marlon.portalusuario.domain.model.DataSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.marlon.portalusuario.domain.model.AppSettings as ModelAppPreferences

@HiltViewModel
class SplashViewModel @Inject constructor(
    preferences: AppPreferencesManager,
    storage: SessionStorage
) : ViewModel() {
    val pref = preferences.preferences().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ModelAppPreferences()
    )
    val session: StateFlow<DataSession?> = storage.dataSession.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )
}
