package com.marlon.portalusuario.presentation.signup

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.auth.AuthService
import com.marlon.portalusuario.util.Utils.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val service: AuthService
) : ViewModel() {
    private val _state = mutableStateOf(SignupState())
    val state: State<SignupState> get() = _state

    private val _currentStep = mutableIntStateOf(0)
    val currentStep: State<Int> get() = _currentStep

    private val _userCreated = mutableStateOf(false)
    val userCreated: State<Boolean> get() = _userCreated

    fun onEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.OnChangedCaptchaCode ->
                _state.value = _state.value.copy(captchaCode = event.value)
            is SignupEvent.OnChangedConfirmCode ->
                _state.value = _state.value.copy(confirmCode = event.value)
            is SignupEvent.OnChangedConfirmPassword ->
                _state.value = _state.value.copy(confirmPassword = event.value)
            is SignupEvent.OnChangedDNI ->
                _state.value = _state.value.copy(dni = event.value)
            is SignupEvent.OnChangedPassword ->
                _state.value = _state.value.copy(password = event.value)
            is SignupEvent.OnChangedPhoneNumber ->
                _state.value = _state.value.copy(phoneNumber = event.value)
            SignupEvent.OnConfirmCode -> confirmCode()
            SignupEvent.OnCreateUser -> createUser()
            SignupEvent.OnLoadCaptcha -> loadCaptcha()
            SignupEvent.OnRegisterUser -> registerUser()
            SignupEvent.OnTogglePasswordVisibility ->
                _state.value = _state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible)
        }
    }

    private fun confirmCode() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { service.validateCodeIdentity(_state.value.confirmCode, _state.value.dni) }
                .onSuccess { if (it.result == "ok") _currentStep.intValue += 1 }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun createUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching { service.createUser(_state.value.password, _state.value.phoneNumber, _state.value.dni) }
                .onSuccess { if (it.result == "ok") _userCreated.value = true }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun loadCaptcha() {
        viewModelScope.launch {
            _state.value = _state.value.copy(error = null)
            _state.value = _state.value.copy(isLoadingCaptcha = true)
            runCatching { service.getCaptcha() }
                .onSuccess {
                    _state.value = _state.value.copy(captchaImage = it.data.toBitmap(), idRequest = it.idRequest)
                }
                .onFailure { _state.value = _state.value.copy(captchaImage = null, error = it.message) }
            _state.value = _state.value.copy(isLoadingCaptcha = false)
        }
    }

    private fun registerUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching {
                service.registerUser(
                    _state.value.phoneNumber,
                    _state.value.dni,
                    _state.value.captchaCode,
                    _state.value.idRequest
                )
            }.onSuccess { if (it.result == "ok") _currentStep.intValue += 1 }
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
