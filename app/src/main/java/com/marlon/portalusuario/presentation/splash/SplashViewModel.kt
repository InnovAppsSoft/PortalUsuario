package com.marlon.portalusuario.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferencesManager
import com.marlon.portalusuario.domain.model.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferences: AppPreferencesManager
) : ViewModel() {
    private val _pref: MutableStateFlow<AppSettings?> = MutableStateFlow(null)
    val pref: StateFlow<AppSettings?>
        get() = _pref.asStateFlow()

    init {
        viewModelScope.launch {
            _pref.value = preferences.preferences().first()
        }
    }
}
