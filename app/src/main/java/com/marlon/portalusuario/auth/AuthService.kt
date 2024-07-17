package com.marlon.portalusuario.auth

import io.github.suitetecsa.sdk.nauta.api.NautaService
import io.github.suitetecsa.sdk.nauta.model.login.LoginRequest
import io.github.suitetecsa.sdk.nauta.model.reset.CreateUserRequest
import io.github.suitetecsa.sdk.nauta.model.reset.RegisterUserRequest
import io.github.suitetecsa.sdk.nauta.model.reset.ResetPasswordRequest
import io.github.suitetecsa.sdk.nauta.model.reset.ValidateCodeIdentityRequest
import io.github.suitetecsa.sdk.nauta.model.reset.ValidateCodeRequest
import io.github.suitetecsa.sdk.nauta.model.reset.ValidateUserRequest

class AuthService(private val service: NautaService) {
    suspend fun getCaptcha() = service.getCaptcha()
    suspend fun auth(phoneNumber: String, password: String, captchaCode: String, idRequest: String) =
        service.login(
            LoginRequest(phoneNumber, password, idRequest = idRequest, captchaCode = captchaCode)
        )

    suspend fun validateUser(phoneNumber: String, captchaCode: String, idRequest: String) =
        service.validateUser(ValidateUserRequest(captchaCode, idRequest, phoneNumber))

    suspend fun confirmCode(phoneNumber: String, confirmCode: String) =
        service.validateConfirmCode(ValidateCodeRequest(phoneNumber, confirmCode))

    suspend fun resetPassword(phoneNumber: String, newPassword: String, confirmCode: String) =
        service.resetPassword(ResetPasswordRequest(phoneNumber, newPassword, confirmCode))

    suspend fun registerUser(phoneNumber: String, dni: String, captchaCode: String, idRequest: String) =
        service.registerUser(RegisterUserRequest(captchaCode, idRequest, dni, phoneNumber))

    suspend fun validateCodeIdentity(confirmCode: String, dni: String) =
        service.validateCodeIdentity(ValidateCodeIdentityRequest(dni, confirmCode))

    suspend fun createUser(password: String, phoneNumber: String, dni: String) =
        service.createUser(CreateUserRequest(phoneNumber, password, dni))
}
