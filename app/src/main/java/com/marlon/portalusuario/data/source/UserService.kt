package com.marlon.portalusuario.data.source

import android.util.Log
import com.marlon.portalusuario.data.extensions.asModel
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.exceptions.SessionException
import com.marlon.portalusuario.util.Utils.createPasswordApp
import io.github.suitetecsa.sdk.exception.NautaException
import io.github.suitetecsa.sdk.nauta.api.NautaService
import io.github.suitetecsa.sdk.nauta.model.User
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest

private const val TAG = "UserService"
private const val MaxIterations = 10

class UserService(private val service: NautaService) {
    suspend fun getUser(authorization: String, userRequest: UsersRequest): ResponseUser {
        var request = userRequest
        var iterations = 0

        while (iterations < MaxIterations) {
            iterations++
            service.users("Bearer $authorization", request, createPasswordApp()).let { response ->
                if (response.result != "ok") throw SessionException(response.result)
                val user = response.user as User
                Log.d(TAG, "getUser: ${user.services.mobileServices}")
                if (user.completed == "true") {
                    return buildResponse(user)
                } else {
                    request = userRequest.copy(lastUpdate = user.lastUpdate)
                }
            }
        }

        throw NautaException("Error obteniendo los datos :: Máximo número de intentos alcanzado sin respuesta")
    }

    private fun buildResponse(user: User) = ResponseUser(
        user.asProfile(),
        user.services.mobileServices.map { it.asModel() },
        user.services.navServices.map { it.asModel() }
    )
}

data class ResponseUser(
    val profile: ClientProfile,
    val mobileServices: List<MobileService>,
    val navServices: List<NavigationService>
)

fun User.asProfile() = ClientProfile(
    client.email,
    client.name,
    client.mailNotifications == "true",
    client.mobileNotifications == "true",
    client.phoneNumber,
    client.portalUser,
    lastUpdate
)
