package com.marlon.portalusuario.testhelpers

import com.marlon.portalusuario.domain.data.UserAccountRepository
import com.marlon.portalusuario.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeUserAccountRepository(
    initialUsers: List<User> = emptyList(),
) : UserAccountRepository {
    private val _allUsers = MutableStateFlow(initialUsers)

    override val allUsers: Flow<List<User>> = _allUsers

    override suspend fun insertUser(user: User) {
        _allUsers.value = _allUsers.value + user
    }

    override suspend fun updateUser(user: User) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == user.id) user else it
        }
    }

    override suspend fun deleteUser(user: User) {
        _allUsers.value = _allUsers.value - user
    }

    override suspend fun deleteAllUsers() {
        _allUsers.value = emptyList()
    }
}
