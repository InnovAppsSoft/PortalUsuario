package com.marlon.portalusuario.data.user

import android.util.Log
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.entity.asModel
import com.marlon.portalusuario.data.source.UserService
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.domain.model.asEntity
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "UserRepositoryImpl"

class UserRepositoryImpl(private val service: UserService, private val dao: ServicesDao) : UserRepository {
    override suspend fun fetchUser(authToken: String, portalUser: String, lastUpdate: String) {
        Log.d(
            TAG,
            "fetchUser: getting user with data: authToken: $authToken, request: ${UsersRequest(portalUser, lastUpdate)}"
        )
        service.getUser(authToken, UsersRequest(portalUser, lastUpdate)).also { response ->
            dao.insertClientProfile(response.profile.asEntity())
            response.mobileServices.forEach { dao.insertMobileServices(it.asEntity()) }
            response.navServices.forEach { dao.insertNavigationServices(it.asEntity()) }
        }
    }

    override fun getClientProfile(): Flow<ClientProfile> =
        dao.getClientProfile().map { it.asModel() }

    override fun getMobileServices(): Flow<List<MobileService>> =
        dao.getMobileServices().map { services -> services.map { it.asModel() } }

    override fun getNavServices(): Flow<List<NavigationService>> =
        dao.getNavigationServices().map { services -> services.map { it.asModel() } }
}
