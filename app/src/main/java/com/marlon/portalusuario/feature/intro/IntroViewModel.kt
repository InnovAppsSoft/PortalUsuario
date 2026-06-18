package com.marlon.portalusuario.feature.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.IAppPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel
    @Inject
    constructor(
        private val appPreferencesManager: IAppPreferencesManager,
    ) : ViewModel() {
        fun onIntroCompleted() {
            viewModelScope.launch {
                appPreferencesManager.updateIsIntroOpened(true)
            }
        }
    }
