package com.marlon.portalusuario.nauta.data.repository

import com.marlon.portalusuario.nauta.data.UserDao
import com.marlon.portalusuario.nauta.data.entities.User

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getUsersFromRoom(onResult: (Users) -> Unit): Users {
        val users = userDao.getUsers()
        onResult(users)
        return users
    }

    override suspend fun getUserFromRoom(id: Int) = userDao.getUser(id)

    override suspend fun getUserFromRoom(userName: String) = userDao.getUser(userName)

    override suspend fun addUserToRoom(user: User) = userDao.addUser(user)

    override suspend fun updateUserInRoom(user: User) = userDao.updateUser(user)

    override suspend fun deleteUserFromRoom(user: User) = userDao.deleteUser(user)
}