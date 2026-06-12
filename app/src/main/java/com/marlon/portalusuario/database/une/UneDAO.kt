package com.marlon.portalusuario.database.une

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.une.Une

@Dao
interface UneDAO {
    @Insert
    fun insertUne(une: Une)

    @Update
    fun updateUne(une: Une)

    @Delete
    fun deleteUne(une: Une)

    @Query("SELECT * FROM une")
    fun getAllUne(): LiveData<List<Une>>

    @Query("DELETE FROM une")
    fun deleteAllUne()
}
