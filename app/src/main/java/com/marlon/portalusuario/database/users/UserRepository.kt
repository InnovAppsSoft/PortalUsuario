package com.marlon.portalusuario.database.users

import android.app.Application
import com.marlon.portalusuario.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class UserRepository(application: Application) {
    private val userDao: UserDAO
    private val database: UserDataBase
    val allUsers: Flow<List<User>>

    init {
        database = UserDataBase.getInstance(application)
        userDao = database.dao()
        allUsers = userDao.getAllUser()
    }

    fun insertUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertUser(user)
        }
    }

    fun updateUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.deleteUser(user)
        }
    }

    fun deleteAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            userDao.deleteAllUser()
        }
    }
}
