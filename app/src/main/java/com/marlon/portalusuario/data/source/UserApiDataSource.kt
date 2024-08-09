package com.marlon.portalusuario.data.source

import android.util.Log
import com.marlon.portalusuario.exceptions.SessionException
import com.marlon.portalusuario.util.Utils.createPasswordApp
import io.github.suitetecsa.sdk.exception.NautaException
import io.github.suitetecsa.sdk.nauta.api.NautaService
import io.github.suitetecsa.sdk.nauta.model.User
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest

private const val TAG = "UserService"
private const val MaxIterations = 10

class UserApiDataSource(private val service: NautaService) {
    suspend fun getUser(authorization: String, userRequest: UsersRequest): User {
        var request = userRequest
        var iterations = 0

        while (iterations < MaxIterations) {
            iterations++
            service.users("Bearer $authorization", request, createPasswordApp()).let { response ->
                if (response.result != "ok") throw SessionException(response.result)
                val user = response.user as User
                Log.d(TAG, "getUser: ${user.services.mobileServices}")
                if (user.completed == "true") {
                    return user
                } else {
                    request = userRequest.copy(lastUpdate = user.lastUpdate)
                }
            }
        }

        throw NautaException("Error obteniendo los datos :: Máximo número de intentos alcanzado sin respuesta")
    }
}
