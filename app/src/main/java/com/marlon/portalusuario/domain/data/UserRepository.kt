package com.marlon.portalusuario.domain.data

import com.marlon.portalusuario.domain.model.ClientProfile
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.NavigationService
import io.github.suitetecsa.sdk.android.model.SimCard
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun fetchUser(simCard: SimCard? = null)
    fun getClientProfile(): Flow<ClientProfile>
    fun getMobileServices(): Flow<List<MobileService>>
    fun getNavServices(): Flow<List<NavigationService>>
}
