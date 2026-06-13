package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.domain.data.UserRepository
import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import io.github.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserRepository(
    initialMobileServices: List<MobileService> = emptyList(),
) : UserRepository {
    private val _mobileServices = MutableStateFlow(initialMobileServices)
    private var _fetchUserCallCount = 0
    private var _lastSimCard: SimCard? = null

    override suspend fun fetchUser(simCard: SimCard?) {
        _fetchUserCallCount++
        _lastSimCard = simCard
    }

    override fun getClientProfile(): Flow<ClientProfile> {
        throw UnsupportedOperationException("Not used in tests")
    }

    override fun getMobileServices(): Flow<List<MobileService>> = _mobileServices

    override fun getNavServices(): Flow<List<NavigationService>> {
        throw UnsupportedOperationException("Not used in tests")
    }

    fun fetchUserCallCount(): Int = _fetchUserCallCount
    fun lastSimCard(): SimCard? = _lastSimCard
}
