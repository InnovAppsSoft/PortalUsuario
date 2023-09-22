package cu.suitetecsa.nautanav.data.network

import cu.suitetecsa.nautanav.core.network.model.ResultType
import cu.suitetecsa.nautanav.core.toData
import cu.suitetecsa.nautanav.core.toEntity
import cu.suitetecsa.nautanav.data.UserDao
import cu.suitetecsa.nautanav.data.entities.toDomain
import cu.suitetecsa.nautanav.domain.dto.UserDTO
import cu.suitetecsa.nautanav.domain.model.UserModel
import cu.suitetecsa.sdk.nauta.core.exceptions.NotLoggedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(private val userDao: UserDao, private val service: NautaService) :
    UserRepository {
    override suspend fun getUsersFromRoom(onResult: (Users) -> Unit): Users {
        val users = userDao.getUsers().map { it.toDomain() }
        onResult(users)
        return users
    }

    override suspend fun getUserFromRoom(id: Int) = userDao.getUser(id)

    override suspend fun getUserFromRoom(userName: String) = userDao.getUser(userName)

    override fun addUserToRoom(user: UserDTO): Flow<ResultType<UserModel>> = flow {
        try {
            val userResponse = service.login(user.username, user.password, user.captchaCode!!)
            userDao.addUser(userResponse.toEntity(password = user.password))
            val lastUser = userDao.getUsers().last().toDomain()
            emit(ResultType.Success(lastUser))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultType.Error(e.message ?: e.javaClass.name))
        }
    }

    override fun updateUserInRoom(user: UserModel): Flow<ResultType<UserModel>> = flow {
        userDao.updateUser(user.toData())
        emit(ResultType.Success(userDao.getUser(user.id).toDomain()))
    }

    override fun updateUserInRoom(user: UserDTO): Flow<ResultType<UserModel>> = flow {
        try {
            val result =
                if (user.captchaCode.isNullOrEmpty()) service.userInformation() else {
                    service.login(user.username, user.password, user.captchaCode)
                }
            userDao.updateUser(result.toEntity(id = user.id, password = user.password))
            emit(ResultType.Success(userDao.getUser(user.id).toDomain()))
        } catch (e: NotLoggedIn) {
            e.printStackTrace()
            emit(ResultType.Error("Not logged in"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultType.Error(e.message ?: "Invalid"))
        }
    }

    override fun deleteUserFromRoom(user: UserDTO): Flow<ResultType<Users>> = flow {
        val userToDelete = userDao.getUser(user.id)
        userDao.deleteUser(userToDelete)
        emit(ResultType.Success(userDao.getUsers().map { it.toDomain() }))
    }
}