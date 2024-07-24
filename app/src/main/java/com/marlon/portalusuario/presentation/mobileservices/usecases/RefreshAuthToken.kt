package com.marlon.portalusuario.presentation.mobileservices.usecases

import android.util.Log
import com.marlon.portalusuario.data.source.AuthService
import com.marlon.portalusuario.exceptions.SessionException
import io.github.suitetecsa.sdk.nauta.model.User
import javax.inject.Inject

private const val TAG = "RefreshAuthToken"

class RefreshAuthToken @Inject constructor(private val authService: AuthService) {
    suspend operator fun invoke(
        number: String,
        password: String,
        code: String,
        idRequest: String
    ): Triple<String, String, Boolean> {
        Log.d(TAG, "invoke: Refreshing Token")
        return authService.auth(number, password, code, idRequest).let {
            if (it.result == "ok") {
                Triple(it.token, (it.user as User).lastUpdate, (it.user as User).updatedServices == "true")
            } else {
                throw SessionException(it.result)
            }
        }
    }
}
