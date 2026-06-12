package com.marlon.portalusuario.domain.data

import com.marlon.portalusuario.model.User
import kotlinx.coroutines.flow.Flow

interface UserAccountRepository {
    val allUsers: Flow<List<User>>

    suspend fun insertUser(user: User)

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)

    suspend fun deleteAllUsers()
}
