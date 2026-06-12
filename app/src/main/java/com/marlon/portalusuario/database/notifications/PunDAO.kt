package com.marlon.portalusuario.database.notifications

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.punotifications.PUNotification
import kotlinx.coroutines.flow.Flow

@Dao
interface PunDAO {
    @Insert
    suspend fun insertPUNotification(post: PUNotification)

    @Update
    suspend fun updatePUNotification(post: PUNotification)

    @Delete
    suspend fun deletePUNotification(post: PUNotification)

    @Query("SELECT * FROM notifications")
    fun getAllPUNotification(): Flow<List<PUNotification>>

    @Query("DELETE FROM notifications")
    suspend fun deleteAllPUNotification()
}
