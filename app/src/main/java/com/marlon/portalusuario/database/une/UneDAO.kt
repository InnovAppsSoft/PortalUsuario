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
    fun insertUne(une: Une)

    @Update
    fun updateUne(une: Une)

    @Delete
    fun deleteUne(une: Une)

    @Query("SELECT * FROM une")
    fun getAllUne(): Flow<List<Une>>

    @Query("DELETE FROM une")
    fun deleteAllUne()
}
