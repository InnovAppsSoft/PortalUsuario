package com.marlon.portalusuario.data.user

import android.annotation.SuppressLint
import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.data.mappers.ClientProfileEntityToDomainMapper
import com.marlon.portalusuario.data.mappers.MobServiceEntityToDomainMapper
import com.marlon.portalusuario.data.mappers.NavServEntityToDomainMapper
import com.marlon.portalusuario.data.source.UserLocalDataSource
import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.NavigationService
import com.marlon.portalusuario.domain.model.ServiceType.Local
import com.marlon.portalusuario.domain.model.ServiceType.LocalAndRemote
import com.marlon.portalusuario.domain.model.ServiceType.Remote
import io.github.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import com.marlon.portalusuario.domain.model.MobileService as MobServDomain

class UserRepositoryImpl(
    private val dao: ServicesDao,
    private val mobServiceEntityToDomainMapper: MobServiceEntityToDomainMapper,
    private val clientProfileEntityToDomainMapper: ClientProfileEntityToDomainMapper,
    private val navServEntityToDomainMapper: NavServEntityToDomainMapper,
    private val localDataSource: UserLocalDataSource = UserLocalDataSource()
) : UserRepository {

    @SuppressLint("MissingPermission")
    private suspend fun fetchUserFromLocal(simCard: SimCard, isRemote: Boolean = false) =
        localDataSource.fetch(simCard, isRemote).let { service ->
            val lastUpdated = System.currentTimeMillis()
            dao.insertMobileServices(service.copy(lastUpdated = lastUpdated))
        }

    override suspend fun fetchUser(simCard: SimCard?) {
        simCard?.also { sim ->
            when (getMobileServices().first()
                .firstOrNull { it.id == "53${sim.phoneNumber!!}" }?.type ?: Local) {
                Local -> fetchUserFromLocal(sim)
                Remote -> {}
                LocalAndRemote -> fetchUserFromLocal(sim, true)
            }
        }
    }

    override fun getClientProfile(): Flow<ClientProfile> =
        dao.getClientProfile().map(clientProfileEntityToDomainMapper::map)

    override fun getMobileServices(): Flow<List<MobServDomain>> =
        dao.getMobileServices().map { services ->
            services.map(mobServiceEntityToDomainMapper::map)
        }

    override fun getNavServices(): Flow<List<NavigationService>> =
        dao.getNavigationServices().map { services ->
            services.map(navServEntityToDomainMapper::map)
        }
}
