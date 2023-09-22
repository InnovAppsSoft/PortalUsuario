package cu.suitetecsa.nautanav.domain

import cu.suitetecsa.nautanav.data.network.UserRepository
import cu.suitetecsa.nautanav.domain.dto.UserDTO
import javax.inject.Inject

class AddUserUseCase @Inject constructor(private val repository: UserRepository) {

    operator fun invoke(userDTO: UserDTO) = repository.addUserToRoom(userDTO)
}