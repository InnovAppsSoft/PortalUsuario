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
    fun insertPUNotification(post: PUNotification)

    @Update
    fun updatePUNotification(post: PUNotification)

    @Delete
    fun deletePUNotification(post: PUNotification)

    @Query("SELECT * FROM notifications")
    fun getAllPUNotification(): Flow<List<PUNotification>>

    @Query("DELETE FROM notifications")
    fun deleteAllPUNotification()
}
