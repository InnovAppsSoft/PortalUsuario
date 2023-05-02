package com.marlon.portalusuario.nauta.data.network

import com.marlon.portalusuario.nauta.core.network.model.ResultType
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.domain.dto.UserDTO
import com.marlon.portalusuario.nauta.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

typealias Users = List<UserModel>


interface UserRepository {
    suspend fun getUsersFromRoom(onResult: (Users) -> Unit): Users
    suspend fun getUserFromRoom(id: Int): User
    suspend fun getUserFromRoom(userName: String): User
    fun addUserToRoom(user: UserDTO): Flow<ResultType<UserModel>>
    fun updateUserInRoom(user: UserModel): Flow<ResultType<UserModel>>
    fun updateUserInRoom(user: UserDTO): Flow<ResultType<UserModel>>
    fun deleteUserFromRoom(user: UserDTO): Flow<ResultType<Users>>
}