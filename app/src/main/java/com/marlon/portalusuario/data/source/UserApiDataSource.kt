package com.marlon.portalusuario.data.source

import android.util.Log
import com.marlon.portalusuario.data.preferences.SessionStorage
import com.marlon.portalusuario.domain.model.DataSession
import com.marlon.portalusuario.presentation.mobileservices.usecases.RefreshAuthToken
import com.marlon.portalusuario.util.Utils.createPasswordApp
import com.marlon.portalusuario.util.Utils.isTokenExpired
import io.github.suitetecsa.sdk.exception.NautaException
import io.github.suitetecsa.sdk.nauta.api.NautaService
import io.github.suitetecsa.sdk.nauta.model.User
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest
import kotlinx.coroutines.flow.first

private const val TAG = "UserApiDataSource"
private const val MaxIterations = 10

class UserApiDataSource(
    private val service: NautaService,
    private val refreshAuthToken: RefreshAuthToken,
    private val sessionStorage: SessionStorage,
) {
    private var refreshToken: Boolean = false
    private var lastUpdate: String? = null

    private suspend fun getAuthToken(session: DataSession): String = session.authToken
        .takeIf { !it.isTokenExpired() && !refreshToken }
        ?: refreshAuthToken(session.phoneNumber, session.password, session.captchaCode, session.idRequest)
            .also {
                lastUpdate = it.second
                refreshToken = false
                sessionStorage.updateDataSession(
                    session.copy(
                        authToken = it.first,
                        lastUpdate = it.second,
                        updatedServices = it.third
                    )
                )
            }.first

    suspend fun fetch(): User {
        Log.d(TAG, "fetchUser: Fetching user from remote")
        val session = sessionStorage.dataSession.first()!!
        var authorization = getAuthToken(session)
        var request = UsersRequest(session.portalUser, lastUpdate ?: session.lastUpdate)
        var iterations = 0

        while (iterations < MaxIterations) {
            val response = service.users("Bearer $authorization", request, createPasswordApp())
            if (response.result != "ok") {
                refreshToken = true
                authorization = getAuthToken(session)
                continue
            }
            iterations++
            val user = response.user as User
            if (user.completed == "true") {
                Log.d(TAG, "getUser: mobileServicesSize :: ${user.services.mobileServices.size}")
                sessionStorage.updateDataSession(session.copy(lastUpdate = lastUpdate!!, updatedServices = true))
                return user
            } else {
                request = request.copy(lastUpdate = user.lastUpdate)
            }
        }

        throw NautaException("Error obteniendo los datos :: Máximo número de intentos alcanzado sin respuesta")
    }
}
