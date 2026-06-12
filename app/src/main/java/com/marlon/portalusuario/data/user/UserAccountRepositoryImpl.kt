package com.marlon.portalusuario.data.user

import com.marlon.portalusuario.database.users.UserDAO
import com.marlon.portalusuario.domain.data.UserAccountRepository
import com.marlon.portalusuario.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserAccountRepositoryImpl
    @Inject
    constructor(
        private val userDao: UserDAO,
    ) : UserAccountRepository {
        override val allUsers: Flow<List<User>> = userDao.getAllUser()

        override suspend fun insertUser(user: User) {
            userDao.insertUser(user)
        }

        override suspend fun updateUser(user: User) {
            userDao.updateUser(user)
        }

        override suspend fun deleteUser(user: User) {
            userDao.deleteUser(user)
        }

        override suspend fun deleteAllUsers() {
            userDao.deleteAllUser()
        }
    }
