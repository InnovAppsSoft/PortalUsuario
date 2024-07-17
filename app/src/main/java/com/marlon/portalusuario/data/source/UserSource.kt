package com.marlon.portalusuario.data.source

import com.marlon.portalusuario.data.extensions.asModel
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.exceptions.SessionException
import io.github.suitetecsa.sdk.nauta.api.NautaService
import io.github.suitetecsa.sdk.nauta.model.User
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest

class UserSource(private val service: NautaService) {
    suspend fun getUser(
        authorization: String,
        userRequest: UsersRequest
    ): Triple<ClientProfile, List<MobileService>, List<NavigationService>> {
        return service.users(authorization, userRequest).let { response ->
            if (response.result != "OK") throw SessionException(response.result)
            val user = response.user as User
            if (user.completed == "true") {
                Triple(
                    ClientProfile(
                        user.client.email,
                        user.client.name,
                        user.client.mailNotifications == "true",
                        user.client.mobileNotifications == "true",
                        user.client.phoneNumber,
                        user.client.portalUser,
                        user.lastUpdate
                    ),
                    user.services.mobileServices.map { it.asModel() },
                    user.services.navServices.map { it.asModel() }
                )
            } else {
                getUser(authorization, userRequest.copy(lastUpdate = user.lastUpdate))
            }
        }
    }
}