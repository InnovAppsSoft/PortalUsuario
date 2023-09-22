package cu.suitetecsa.nautanav.data.network

import cu.suitetecsa.nautanav.core.network.model.ResultType
import cu.suitetecsa.nautanav.data.entities.User
import cu.suitetecsa.nautanav.domain.dto.UserDTO
import cu.suitetecsa.nautanav.domain.model.UserModel
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