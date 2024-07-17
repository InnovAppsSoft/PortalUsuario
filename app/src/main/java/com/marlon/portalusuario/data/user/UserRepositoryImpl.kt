package com.marlon.portalusuario.data.user

import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.entity.asModel
import com.marlon.portalusuario.data.preferences.AppPreferences
import com.marlon.portalusuario.data.preferences.ServicesPreference
import com.marlon.portalusuario.data.source.UserSource
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.domain.model.asEntity
import io.github.suitetecsa.sdk.nauta.model.users.UsersRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userSource: UserSource,
    private val preferences: AppPreferences,
    private val servicesDao: ServicesDao
) : UserRepository {
    override suspend fun fetchUser() {
        val (profile, mobileServices, navServices) = userSource.getUser(
            preferences.preferences().first().dataSession.authToken!!,
            UsersRequest(
                preferences.preferences().first().dataSession.portalUser!!,
                preferences.preferences().first().dataSession.lastUpdate!!
            )
        )
        servicesDao.insertClientProfile(profile.asEntity())
        mobileServices.forEach { servicesDao.insertMobileServices(it.asEntity()) }
        navServices.forEach { servicesDao.insertNavigationServices(it.asEntity()) }
    }

    override fun getClientProfile(): Flow<List<ClientProfile>> =
        servicesDao.getClientProfile().map { profile -> profile.map { it.asModel() } }

    override fun getMobileServices(): Flow<List<MobileService>> =
        servicesDao.getMobileServices().map { services -> services.map { it.asModel() } }

    override fun getNavServices(): Flow<List<NavigationService>> =
        servicesDao.getNavigationServices().map { services -> services.map { it.asModel() } }
}
