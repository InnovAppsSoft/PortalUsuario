package com.marlon.portalusuario.nauta.domain

import com.marlon.portalusuario.nauta.data.network.UserRepository
import com.marlon.portalusuario.nauta.domain.dto.UserDTO
import javax.inject.Inject

class AddUserUseCase @Inject constructor(private val repository: UserRepository) {

    operator fun invoke(userDTO: UserDTO) = repository.addUserToRoom(userDTO)
}