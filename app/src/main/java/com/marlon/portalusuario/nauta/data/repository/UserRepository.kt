package com.marlon.portalusuario.nauta.data.repository

import com.marlon.portalusuario.nauta.data.entities.User
import kotlinx.coroutines.flow.Flow

typealias Users = List<User>

interface UserRepository {
    suspend fun getUsersFromRoom(onResult: (Users) -> Unit): Users
    suspend fun getUserFromRoom(id: Int): User
    suspend fun getUserFromRoom(userName: String): User
    suspend fun addUserToRoom(user: User)
    suspend fun updateUserInRoom(user: User)
    suspend fun deleteUserFromRoom(user: User)
}