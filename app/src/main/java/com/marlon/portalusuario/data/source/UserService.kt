package com.marlon.portalusuario.data.source

import android.util.Log
import com.marlon.portalusuario.data.extensions.asModel
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.exceptions.SessionException
import io.github.suitetecsa.sdk.nauta.api.NautaService
import io.github.suitetecsa.sdk.nauta.model.User
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest

private const val TAG = "UserService"

class UserService(private val service: NautaService) {
    suspend fun getUser(authorization: String, userRequest: UsersRequest): ResponseUser =
        service.users("Bearer $authorization", userRequest).let { response ->
            Log.d(TAG, "getUser: $response")
            if (response.result != "ok") throw SessionException(response.result)
            (response.user as User).let { user ->
                if (user.completed == "true") {
                    ResponseUser(
                        user.asProfile(),
                        user.services.mobileServices.map { it.asModel() },
                        user.services.navServices.map { it.asModel() }
                    )
                } else {
                    Log.d(
                        TAG,
                        "fetchUser: getting user with data: authToken:" +
                            " $authorization, request: ${userRequest.copy(lastUpdate = user.lastUpdate)}"
                    )
                    getUser(authorization, userRequest.copy(lastUpdate = user.lastUpdate))
                }
            }
        }
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
