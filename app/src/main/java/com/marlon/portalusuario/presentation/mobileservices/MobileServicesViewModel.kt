package com.marlon.portalusuario.presentation.mobileservices

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.presentation.mobileservices.usecases.RefreshAuthToken
import com.marlon.portalusuario.util.Utils.isTokenExpired
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.exception.InvalidSessionException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.marlon.portalusuario.domain.model.AppPreferences as ModelAppPreferences

private const val TAG = "MobileServicesViewModel"

@HiltViewModel
class MobileServicesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val repository: UserRepository,
    private val refreshToken: RefreshAuthToken
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

    private var _state = mutableStateOf(MobileServicesState())
    val state: State<MobileServicesState> get() = _state

    fun onEvent(event: MobileServicesEvent) {
        when (event) {
            MobileServicesEvent.OnUpdate -> update()
            is MobileServicesEvent.OnChangeCurrentMobileService -> viewModelScope.launch {
                appPreferences.updateMobileServiceSelectedId(event.value)
                Log.d(TAG, "onEvent: changed serviceId: ${event.value}")
            }
        }
    }

    private fun update(updateToken: Boolean = false) {
        viewModelScope.launch {
            preferences.value.dataSession?.let { data ->
                Log.d(TAG, "update: updating services with data: $data")
                _state.value = _state.value.copy(isLoading = true)
                runCatching {
                    val authToken = data.authToken.takeIf { !it.isTokenExpired() && !updateToken }
                        ?: refreshToken(
                            data.phoneNumber,
                            data.password,
                            data.captchaCode,
                            data.idRequest
                        )
                            .also {
                                Log.d(TAG, "update: Triple :: $it")
                                appPreferences.updateDataSession(
                                    data.copy(
                                        authToken = it.first,
                                        lastUpdate = it.second,
                                        updatedServices = it.third
                                    )
                                )
                            }.first
                    repository.fetchUser(authToken, data.portalUser, data.lastUpdate)
                }.onFailure {
                    it.printStackTrace()
                    if (it is InvalidSessionException) {
                        update(true)
                    }
                }.onSuccess {
                    Log.d(TAG, "update: updating dataSession...")
                    appPreferences.updateDataSession(
                        data.copy(
                            updatedServices = true,
                            lastUpdate = repository.getClientProfile().first().lastUpdate
                        )
                    )
                    Log.d(TAG, "update: dataSession updated!")
                }
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }
}
