package com.marlon.portalusuario.presentation.resetpassword

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.data.source.AuthService
import com.marlon.portalusuario.util.Utils.toBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private val _state = mutableStateOf(ResetPasswordState())
    val state: State<ResetPasswordState> get() = _state

    private val _currentStep = mutableIntStateOf(0)
    val currentStep: State<Int> get() = _currentStep

    private val _passwordRestored = mutableStateOf(false)
    val passwordRestored: State<Boolean> get() = _passwordRestored

    fun onEvent(event: ResetPasswordEvent) {
        when (event) {
            is ResetPasswordEvent.OnChangeCaptchaCode ->
                _state.value = _state.value.copy(captchaCode = event.value)
            is ResetPasswordEvent.OnChangeConfirmCode ->
                _state.value = _state.value.copy(confirmCode = event.value)
            is ResetPasswordEvent.OnChangeConfirmPassword ->
                _state.value = _state.value.copy(confirmPassword = event.value)
            is ResetPasswordEvent.OnChangeNewPassword ->
                _state.value = _state.value.copy(newPassword = event.value)
            is ResetPasswordEvent.OnChangeUser ->
                _state.value = _state.value.copy(phoneNumber = event.value)
            ResetPasswordEvent.OnLoadCaptcha -> loadCaptcha()
            ResetPasswordEvent.OnResetPassword -> resetPassword()
            ResetPasswordEvent.OnTogglePasswordVisibility ->
                _state.value = _state.value.copy(isPasswordVisible = !_state.value.isPasswordVisible)
            ResetPasswordEvent.OnValidateConfirmCode -> validateConfirmCode()
            ResetPasswordEvent.OnValidateUser -> validateUser()
        }
    }

    private fun loadCaptcha() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingCaptcha = true)
            runCatching { authService.getCaptcha() }
                .onSuccess { _state.value = _state.value.copy(captchaImage = it.data.toBitmap()) }
                .onFailure { _state.value = _state.value.copy(captchaImage = null, error = it.message) }
            _state.value = _state.value.copy(isLoadingCaptcha = false)
        }
    }

    private fun resetPassword() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching {
                authService.resetPassword(_state.value.phoneNumber, _state.value.newPassword, _state.value.confirmCode)
            }.onSuccess { if (it.result == "ok") _passwordRestored.value = true }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun validateConfirmCode() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            runCatching {
                authService.confirmCode(_state.value.phoneNumber, _state.value.confirmCode)
            }.onSuccess { if (it.result == "ok") _currentStep.intValue += 1 }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun validateUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            if (isPhoneNumberValid && isCaptchaCodeValid) {
                runCatching {
                    authService.validateUser(
                        _state.value.phoneNumber,
                        _state.value.captchaCode,
                        _state.value.idRequest
                    )
                }.onSuccess { if (it.result == "ok") _currentStep.intValue += 1 }
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private val isPhoneNumberValid: Boolean
        get() = _state.value.phoneNumber.length == 8 &&
            (
                _state.value.phoneNumber.startsWith("5") ||
                    _state.value.phoneNumber.startsWith("6")
                )

    private val isCaptchaCodeValid: Boolean get() = _state.value.captchaCode.length >= 4
}
