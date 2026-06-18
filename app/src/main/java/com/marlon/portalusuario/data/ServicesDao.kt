package com.marlon.portalusuario.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.data.entity.ClientProfile
import com.marlon.portalusuario.data.entity.MobileService
import com.marlon.portalusuario.data.entity.NavigationService
import com.marlon.portalusuario.domain.model.User
import com.marlon.portalusuario.feature.notifications.PUNotification
import com.marlon.portalusuario.feature.une.Une
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

    // User account

    @Query("SELECT * FROM \"user\"")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM \"user\"")
    suspend fun deleteAllUsers()

    // UNE

    @Query("SELECT * FROM une")
    fun getAllUnes(): Flow<List<Une>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUne(une: Une)

    @Update
    suspend fun updateUne(une: Une)

    @Delete
    suspend fun deleteUne(une: Une)

    @Query("DELETE FROM une")
    suspend fun deleteAllUnes()

    // PUN notifications

    @Query("SELECT * FROM notifications")
    fun getAllPUNotifications(): Flow<List<PUNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPUNotification(pun: PUNotification)

    @Update
    suspend fun updatePUNotification(pun: PUNotification)

    @Delete
    suspend fun deletePUNotification(pun: PUNotification)

    @Query("DELETE FROM notifications")
    suspend fun deleteAllPUNotifications()
}
