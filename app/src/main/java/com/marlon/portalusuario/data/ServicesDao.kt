package com.marlon.portalusuario.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.marlon.portalusuario.data.entity.ClientProfile
import com.marlon.portalusuario.data.entity.MobileService
import com.marlon.portalusuario.data.entity.NavigationService
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicesDao {
    @Query("SELECT * FROM client_profile LIMIT 1")
    fun getClientProfile(): Flow<ClientProfile>

    @Query("SELECT * FROM mobile_service")
    fun getMobileServices(): Flow<List<MobileService>>

    @Query("SELECT * FROM navigation_service")
    fun getNavigationServices(): Flow<List<NavigationService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientProfile(clientProfile: ClientProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMobileServices(mobileService: MobileService)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNavigationServices(navigationService: NavigationService)

    @Delete
    suspend fun deleteClientProfile(clientProfile: ClientProfile)

    @Delete
    suspend fun deleteMobileServices(mobileService: MobileService)

    @Delete
    suspend fun deleteNavigationServices(navigationService: NavigationService)
}
