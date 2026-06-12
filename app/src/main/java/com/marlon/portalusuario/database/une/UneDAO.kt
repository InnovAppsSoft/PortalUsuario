package com.marlon.portalusuario.database.une

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.une.Une
import kotlinx.coroutines.flow.Flow

@Dao
interface UneDAO {
    @Insert
    suspend fun insertUne(une: Une)

    @Update
    suspend fun updateUne(une: Une)

    @Delete
    suspend fun deleteUne(une: Une)

    @Query("SELECT * FROM une")
    fun getAllUne(): Flow<List<Une>>

    @Query("DELETE FROM une")
    suspend fun deleteAllUne()
}
