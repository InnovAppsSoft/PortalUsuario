package com.marlon.portalusuario.nauta.data.network

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marlon.portalusuario.nauta.data.UserDao
import com.marlon.portalusuario.nauta.data.entities.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDb : RoomDatabase() {
    abstract val userDao: UserDao
}