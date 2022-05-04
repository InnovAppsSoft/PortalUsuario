package com.marlon.portalusuario.database.une;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marlon.portalusuario.une.Une;

import java.util.List;

@Dao
public interface UneDAO {

    @Insert
    void insertUne(Une Une);

    @Update
    void updateUne(Une Une);

    @Delete
    void deleteUne(Une Une);

    @Query("SELECT * FROM une")
    LiveData<List<Une>> getAllUne();

    @Query("DELETE FROM une")
    void deleteAllUne();
}
