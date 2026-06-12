package com.marlon.portalusuario.data.user

import com.marlon.portalusuario.data.ServicesDao
import com.marlon.portalusuario.domain.data.UserAccountRepository
import com.marlon.portalusuario.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserAccountRepositoryImpl
    @Inject
    constructor(
        private val dao: ServicesDao,
    ) : UserAccountRepository {
        override val allUsers: Flow<List<User>> = dao.getAllUsers()

        override suspend fun insertUser(user: User) {
            dao.insertUser(user)
        }

        override suspend fun updateUser(user: User) {
            dao.updateUser(user)
        }

        override suspend fun deleteUser(user: User) {
            dao.deleteUser(user)
        }

        override suspend fun deleteAllUsers() {
            dao.deleteAllUsers()
        }
    }
