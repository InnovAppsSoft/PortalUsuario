package com.marlon.portalusuario.presentation.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.data.source.AuthService
import com.marlon.portalusuario.domain.model.DataSession
import com.marlon.portalusuario.util.Utils.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.suitetecsa.sdk.nauta.model.User
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val service: AuthService,
    private val preferences: AppPreferences
) : ViewModel() {
    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> get() = _state

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> get() = _isLoggedIn

    fun onEvent(event: AuthEvent) {
        when (event) {
            AuthEvent.OnAuth -> auth()
            is AuthEvent.OnChangeCaptchaCode ->
                _state.value = _state.value.copy(captchaCode = event.value)

            is AuthEvent.OnChangePassword ->
                _state.value = _state.value.copy(password = event.value)

            is AuthEvent.OnChangePhoneNumber ->
                _state.value = _state.value.copy(phoneNumber = event.value)

            AuthEvent.OnLoadCaptcha -> loadCaptcha()
            AuthEvent.OnTogglePasswordVisibility ->
                _state.value =
                    _state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible)

            AuthEvent.OnErrorDismiss ->
                _state.value = _state.value.copy(error = null)
        }
    }

    private fun auth() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            runCatching {
                service.auth(
                    _state.value.phoneNumber,
                    _state.value.password,
                    _state.value.captchaCode,
                    _state.value.idRequest!!
                )
            }
                .onSuccess {
                    when (it.result) {
                        "ok" -> {
                            preferences.updateDataSession(
                                DataSession(
                                    _state.value.phoneNumber,
                                    _state.value.password,
                                    _state.value.captchaCode,
                                    _state.value.idRequest!!,
                                    it.token,
                                    (it.user as User).lastUpdate,
                                    (it.user as User).client.portalUser,
                                    (it.user as User).updatedServices == "true"
                                )
                            )
                            _isLoggedIn.value = true
                        }
                        "errorCaptcha" -> {
                            _state.value = state.value.copy(error = "Codigo captcha incorrecto")
                        }
                        else -> {
                            _state.value = state.value.copy(error = "Usuario o contraseña incorrectos")
                        }
                    }
                }
                .onFailure { it.printStackTrace() }
            _state.value = state.value.copy(isLoading = false)
        }
    }

    private fun loadCaptcha() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoadingCaptcha = true)
            runCatching { service.getCaptcha() }
                .onFailure {
                    _state.value = state.value.copy(error = it.message, captchaImage = null)
                }
                .onSuccess {
                    _state.value = _state.value.copy(
                        captchaImage = it.data.toBitmap(), idRequest = it.idRequest
                    )
                }
            _state.value = state.value.copy(isLoadingCaptcha = false)
        }
    }
}
