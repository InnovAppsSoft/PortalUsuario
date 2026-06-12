package com.marlon.portalusuario.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.PUNotifications.PUNotification
import com.marlon.portalusuario.database.notifications.PunRepository
import com.marlon.portalusuario.database.une.UneRepository
import com.marlon.portalusuario.database.users.UserRepository
import com.marlon.portalusuario.model.User
import com.marlon.portalusuario.une.Une
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("TooManyFunctions")
class PunViewModel
    @Inject
    constructor(
        application: Application,
    ) : AndroidViewModel(application) {
        private val punRepository = PunRepository(application)
        private val userRepository = UserRepository(application)
        private val uneRepository = UneRepository(application)

        // PUN
        val allPUN: StateFlow<List<PUNotification>> =
            punRepository.allPUN
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList(),
                )

        val allPUNLiveData: LiveData<List<PUNotification>> = allPUN.asLiveData()

        fun insertPUN(pun: PUNotification?) {
            viewModelScope.launch {
                pun?.let { punRepository.insertPUNotification(it) }
            }
        }

        fun updatePUN(pun: PUNotification) {
            viewModelScope.launch {
                punRepository.updatePUNotification(pun)
            }
        }

        fun deletePUN(pun: PUNotification) {
            viewModelScope.launch {
                punRepository.deletePUNotification(pun)
            }
        }

        fun deleteAllPUN() {
            viewModelScope.launch {
                punRepository.deleteAllPUNotifications()
            }
        }

        // USERS
        val allUsers: StateFlow<List<User>> =
            userRepository.allUsers
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList(),
                )

        val allUsersLiveData: LiveData<List<User>> = allUsers.asLiveData()

        fun insertUser(user: User) {
            viewModelScope.launch {
                userRepository.insertUser(user)
            }
        }

        fun updateUser(user: User) {
            viewModelScope.launch {
                userRepository.updateUser(user)
            }
        }

        fun deleteUser(user: User) {
            viewModelScope.launch {
                userRepository.deleteUser(user)
            }
        }

        fun deleteAllUsers() {
            viewModelScope.launch {
                userRepository.deleteAllUsers()
            }
        }

        // UNE
        val allUnes: StateFlow<List<Une>> =
            uneRepository.allUnes
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList(),
                )

        val allUnesLiveData: LiveData<List<Une>> = allUnes.asLiveData()

        fun insertUne(une: Une) {
            viewModelScope.launch {
                uneRepository.insertUne(une)
            }
        }

        fun updateUne(une: Une) {
            viewModelScope.launch {
                uneRepository.updateUne(une)
            }
        }

        fun deleteUne(une: Une) {
            viewModelScope.launch {
                uneRepository.deleteUne(une)
            }
        }

        fun deleteAllUnes() {
            viewModelScope.launch {
                uneRepository.deleteAllUnes()
            }
        }
    }
