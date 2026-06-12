package com.marlon.portalusuario.database.users

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marlon.portalusuario.model.User

@Dao
interface UserDAO {
    @Insert
    fun insertUser(user: User)

    @Update
    fun updateUser(user: User)

    @Delete
    fun deleteUser(user: User)

    @Query("SELECT * FROM user")
    fun getAllUser(): LiveData<List<User>>

    @Query("DELETE FROM user")
    fun deleteAllUser()
}
