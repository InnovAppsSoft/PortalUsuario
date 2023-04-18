package com.marlon.portalusuario.nauta.ui

import android.os.CountDownTimer
import androidx.lifecycle.*
import com.marlon.portalusuario.Pref
import com.marlon.portalusuario.commons.toLocalUser
import com.marlon.portalusuario.logging.JCLogging
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.data.network.NautaService
import com.marlon.portalusuario.nauta.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NautaViewModel @Inject constructor(
    private val service: NautaService,
    private val repository: UserRepository,
    private val pref: Pref
) : ViewModel() {
    val captchaImage = MutableLiveData<ByteArray>()
    val isCaptchaLoaded = MutableLiveData<Boolean>()
    val isLogged = MutableLiveData<Boolean>()

    val leftTime = MutableLiveData<String>()
    val isLoggedIn = MutableLiveData<Boolean>()
    val users = repository.getUsersFromRoom().asLiveData()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser
    val status = MutableLiveData<Pair<Boolean, String?>>()

    fun onCreate() {
        viewModelScope.launch {
            // Set init value for the variables
            isLoggedIn.postValue(false)

            // Getting data session if exists
            val dataSession = pref.getSession()
            if (dataSession.isNotEmpty()) {
                service.setDataSession(dataSession)
                _currentUser.postValue(repository.getUserFromRoom(dataSession["username"]!!.split("@")[0]))
                isLoggedIn.postValue(true)
            }
        }
    }

    fun onUserSelected(userIndex: () -> Int) {
        _currentUser.postValue((users.value?.get(userIndex()) ?: 0) as User?)
    }

    fun countDown() {
        viewModelScope.launch {
            try {
                val time = service.getRemainingTime()
                JCLogging.message("Initial time value", time)
                val (hours, minutes, seconds) = time.split(":")
                val millisecondsLeft: Long = (hours.toInt() * 3600000) + (minutes.toInt() * 60000) + (seconds.toInt() * 1000).toLong()
                object : CountDownTimer(millisecondsLeft, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val h = (millisUntilFinished / 3600000).toInt()
                        val m = (millisUntilFinished - h * 3600000).toInt() / 60000
                        val s = (millisUntilFinished - h * 3600000 - m * 60000).toInt() / 1000
                        leftTime.postValue(String.format("%02d:%02d:%02d", h, m, s))
                    }

                    override fun onFinish() {
                        leftTime.postValue("00:00:00")
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
                JCLogging.error(null, null, null)
                JCLogging.error(null, null, e)
            }
        }
    }

    fun connect(userName: String, password: String) {
        viewModelScope.launch {
            try {
                service.connect(userName, password)
                status.postValue(Pair(true, null))
                pref.saveSession(service.getDataSession())
                isLoggedIn.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                status.postValue(Pair(false, e.message))
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            try {
                service.disconnect()
                status.postValue(Pair(true, null))
                isLoggedIn.postValue(false)
                pref.removeSession(pref.getSession())
            } catch (e: Exception) {
                e.printStackTrace()
                status.postValue(Pair(false, e.message))
            }
        }
    }

    fun getCaptcha() {
        viewModelScope.launch {
            isCaptchaLoaded.postValue(false)
            captchaImage.postValue(service.getCaptcha())
            isCaptchaLoaded.postValue(true)
        }
    }

    fun login(userName: String, password: String, captchaCode: String) {
        viewModelScope.launch {
            try {
                val user = service.login(userName, password, captchaCode)
                status.postValue(Pair(true, null))
                isLogged.postValue(true)
                repository.updateUserInRoom(user.toLocalUser(_currentUser.value!!))
                _currentUser.postValue(repository.getUserFromRoom(_currentUser.value!!.id))
            } catch (e: Exception) {
                status.postValue(Pair(false, e.message))
            }
        }
    }

    fun toUp(rechargeCode: String) {
        viewModelScope.launch {
            try {
                service.toUp(rechargeCode)
                status.postValue(Pair(true, null))
            } catch (e: Exception) {
                e.printStackTrace()
                status.postValue(Pair(false, e.message))
            }
        }
    }

    fun transfer(amount: Float, destinationAccount: String) {
        viewModelScope.launch {
            try {
                service.transfer(amount, destinationAccount)
                status.postValue(Pair(true, null))
            } catch (e: Exception) {
                e.printStackTrace()
                status.postValue(Pair(false, e.message))
            }
        }
    }

    // Database
    fun addUser(user: User) {
        viewModelScope.launch {
            repository.addUserToRoom(user)
        }
    }

    fun getUser(id: Int) {
        viewModelScope.launch {
            _currentUser.postValue(repository.getUserFromRoom(id))
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.updateUserInRoom(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUserFromRoom(user)
        }
    }
}