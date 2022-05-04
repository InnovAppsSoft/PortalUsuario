package com.marlon.portalusuario.database.notifications;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.marlon.portalusuario.PUNotifications.PUNotification;

import java.util.List;

@Dao
public interface PunDAO {

    @Insert
    void insertPUNotification(PUNotification post);

    @Update
    void updatePUNotification(PUNotification post);

    @Delete
    void deletePUNotification(PUNotification post);

    @Query("SELECT * FROM notifications")
    LiveData<List<PUNotification>> getAllPUNotification();

    @Query("DELETE FROM notifications")
    void deleteAllPUNotification();
}
