package com.marlon.portalusuario.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.domain.model.DataSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.marlon.portalusuario.domain.model.AppPreferences as ModelAppPreferences

@HiltViewModel
class SplashViewModel @Inject constructor(
    preferences: AppPreferences
) : ViewModel() {
    val pref = preferences.preferences().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        ModelAppPreferences()
    )
}
