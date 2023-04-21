package com.marlon.portalusuario.nauta.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.*
import com.marlon.portalusuario.Pref
import com.marlon.portalusuario.commons.toLocalUser
import com.marlon.portalusuario.logging.JCLogging
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.data.network.NautaService
import com.marlon.portalusuario.nauta.data.repository.UserRepository
import cu.suitetecsa.sdk.nauta.domain.util.priceStringToFloat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NautaViewModel @Inject constructor(
    private val service: NautaService,
    private val repository: UserRepository,
    private val pref: Pref
) : ViewModel() {

    //
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var limiterCountDownTimer: CountDownTimer

    // Captcha
    private val _isLoadingCaptcha = MutableLiveData<Boolean>()
    private val _captchaImage = MutableLiveData<Bitmap>()
    private val _captchaLoadStatus = MutableLiveData<Pair<Boolean, String?>>()

    val isLoadingCaptcha: LiveData<Boolean> = _isLoadingCaptcha
    val captchaImage: LiveData<Bitmap> = _captchaImage
    val captchaLoadStatus: LiveData<Pair<Boolean, String?>> = _captchaLoadStatus

    // Connect
    private val _leftTime = MutableLiveData<String>()
    private val _limitedTime = MutableLiveData<String>()
    private val _isConnecting = MutableLiveData<Boolean>()
    private val _connectStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _showTimePickerDialog = MutableLiveData<Boolean>()

    val leftTime: LiveData<String> = _leftTime
    val limitedTime: LiveData<String> = _limitedTime
    val isConnecting: LiveData<Boolean> = _isConnecting
    val connectStatus: LiveData<Pair<Boolean, String?>> = _connectStatus
    val showTimePickerDialog: LiveData<Boolean> = _showTimePickerDialog

    // User
    private val _isLogging = MutableLiveData<Boolean>()
    private val _isRecharging = MutableLiveData<Boolean>()
    private val _isTransferring = MutableLiveData<Boolean>()
    private val _rechargeCode = MutableLiveData<TextFieldValue>()
    private val _destinationAccount = MutableLiveData<TextFieldValue>()
    private val _amount = MutableLiveData<TextFieldValue>()
    private val _isEnabledRechargeButton = MutableLiveData<Boolean>()
    private val _isEnabledTransferButton = MutableLiveData<Boolean>()
    private val _loginStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _rechargeStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _transferStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _showCaptchaDialog = MutableLiveData<Boolean>()
    private val _currentUser = MutableLiveData<User>()

    val isLogging: LiveData<Boolean> = _isLogging
    val isRecharging: LiveData<Boolean> = _isRecharging
    val rechargeCode: LiveData<TextFieldValue> = _rechargeCode
    val destinationAccount: LiveData<TextFieldValue> = _destinationAccount
    val amount: LiveData<TextFieldValue> = _amount
    val isEnabledRechargeButton: LiveData<Boolean> = _isEnabledRechargeButton
    val isEnabledTransferButton: LiveData<Boolean> = _isEnabledTransferButton
    val isTransferring: LiveData<Boolean> = _isTransferring
    val loginStatus: LiveData<Pair<Boolean, String?>> = _loginStatus
    val rechargeStatus: LiveData<Pair<Boolean, String?>> = _rechargeStatus
    val transferStatus: LiveData<Pair<Boolean, String?>> = _transferStatus
    val showCaptchaDialog: LiveData<Boolean> = _showCaptchaDialog
    val users = repository.getUsersFromRoom().asLiveData()
    val currentUser: LiveData<User> = _currentUser

    // Add user
    private val _userName = MutableLiveData<TextFieldValue>()
    private val _password = MutableLiveData<String>()
    private val _captchaCode = MutableLiveData<String>()
    private val _loginEnable = MutableLiveData<Boolean>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _isLoggedIn = MutableLiveData<Boolean>()
    private val _suggestion: MutableState<String> = mutableStateOf("")

    val userName: LiveData<TextFieldValue> = _userName
    val password: LiveData<String> = _password
    val captchaCode: LiveData<String> = _captchaCode
    val loginEnable: LiveData<Boolean> = _loginEnable
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    val isLoading: LiveData<Boolean> = _isLoading

    fun onCreate() {
        viewModelScope.launch {
            // Getting data session if exists
            val dataSession = pref.getSession()
            if (dataSession.isNotEmpty()) {
                service.setDataSession(dataSession)
                _currentUser.postValue(repository.getUserFromRoom(dataSession["username"]!!.split("@")[0]))
                _isLoggedIn.postValue(true)
            }
            if (currentUser.value == null) {
                try {
                    _isLoadingCaptcha.postValue(true)
                    _captchaImage.postValue(convertImageByteArrayToBitmap(service.getCaptcha()))
                    _isLoadingCaptcha.postValue(false)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isLoadingCaptcha.postValue(false)
                    _captchaLoadStatus.postValue(Pair(false, e.message))
                }
            }
        }
    }

    fun showCaptchaDialog(isShow: Boolean) {
        _showCaptchaDialog.postValue(isShow)
    }

    fun showTimePickerDialog(isShow: Boolean) {
        _showTimePickerDialog.postValue(isShow)
    }

    fun onChangeLimiterTime(newLimiterTime: Pair<Int, Int>) {
        val (hour, minute) = newLimiterTime
        _limitedTime.postValue(String.format("%02d:%02d:00", hour, minute))
    }

    fun onUserSelected(user: User) {
        _currentUser.postValue(user)
    }

    fun onChangeRechargeCode(rechargeCode: TextFieldValue) {
        if (rechargeCode.text.length <= 16) {
            _rechargeCode.postValue(rechargeCode)
        }
        _isEnabledRechargeButton.postValue(rechargeCode.text.length == 12 || rechargeCode.text.length == 16)
    }

    fun onTransferChange(destinationAccount: TextFieldValue, amount: TextFieldValue) {
        _destinationAccount.postValue(destinationAccount)
        _amount.postValue(amount)
        if ((destinationAccount.text.contains("@nauta.com.cu") || destinationAccount.text.contains("@nauta.co.cu")) && amount.text.toFloat() < priceStringToFloat(
                currentUser.value!!.credit
            )
        ) {
            _isEnabledTransferButton.postValue(true)
        } else {
            _isEnabledTransferButton.postValue(false)
        }
    }

    private fun countDown(leftTime: MutableLiveData<String>): CountDownTimer {
        JCLogging.message("Initial time value", leftTime.value)
        val (hours, minutes, seconds) = leftTime.value!!.split(":")
        val millisecondsLeft: Long =
            (hours.toInt() * 3600000) + (minutes.toInt() * 60000) + (seconds.toInt() * 1000).toLong()
        return object : CountDownTimer(millisecondsLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val h = (millisUntilFinished / 3600000).toInt()
                val m = (millisUntilFinished - h * 3600000).toInt() / 60000
                val s = (millisUntilFinished - h * 3600000 - m * 60000).toInt() / 1000
                leftTime.postValue(String.format("%02d:%02d:%02d", h, m, s))
            }

            override fun onFinish() {
                leftTime.postValue("00:00:00")
                disconnect()
            }
        }
    }

    fun connect(userName: String, password: String) {
        viewModelScope.launch {
            try {
                service.connect(userName, password)
                _connectStatus.postValue(Pair(true, null))
                pref.saveSession(service.getDataSession())
                _isLoggedIn.postValue(true)
                _leftTime.postValue(service.getRemainingTime())
                countDownTimer = countDown(_leftTime)
                limiterCountDownTimer = if (_limitedTime.value != null) {
                    countDown(_limitedTime)
                } else {
                    countDownTimer
                }
                countDownTimer.start()
                limiterCountDownTimer.start()
            } catch (e: Exception) {
                e.printStackTrace()
                _connectStatus.postValue(Pair(false, e.message.toString()))
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            try {
                service.disconnect()
                _connectStatus.postValue(Pair(true, null))
                _isLoggedIn.postValue(false)
                pref.removeSession(pref.getSession())
            } catch (e: Exception) {
                e.printStackTrace()
                _connectStatus.postValue(Pair(false, e.message))
            }
        }
    }

    private fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    fun onLoginChanged(userName: TextFieldValue, password: String, captchaCode: String) {
        val internationalSuggestion = "@nauta.com.cu"
        val nationalSuggestion = "@nauta.co.cu"

        _loginStatus.postValue(Pair(true, null))

        if (userName.text.isNotEmpty()) {
            val textInput =
                userName.text.substring(0 until userName.selection.start)
            val inputLength = textInput.length
            if (textInput.contains("@")) {
                val user = textInput.split("@").first()
                val userSuffix = textInput.substring(user.length until inputLength)
                if (internationalSuggestion.contains(userSuffix)) {
                    _suggestion.value = internationalSuggestion.substring(userSuffix.length)
                } else if (nationalSuggestion.contains(userSuffix)) {
                    _suggestion.value = nationalSuggestion.substring(userSuffix.length)
                } else {
                    _suggestion.value = ""
                }
            } else {
                _suggestion.value = internationalSuggestion
            }
            if (textInput.isEmpty()) _suggestion.value = ""
            _userName.value = TextFieldValue(
                text = "$textInput${_suggestion.value}",
                selection = TextRange(inputLength)
            )
        } else {
            _userName.value = userName
        }
        _password.value = password
        _captchaCode.value = captchaCode
        _loginEnable.value =
            isValidEmail(userName) && isValidPassword(password) && isValidCaptchaCode(captchaCode)
    }

    private fun isValidCaptchaCode(captchaCode: String): Boolean = captchaCode.length in 4..10

    private fun isValidPassword(password: String): Boolean = password.length in 8..16

    private fun isValidEmail(userName: TextFieldValue): Boolean =
        userName.text.endsWith("@nauta.com.cu") || userName.text.endsWith("@nauta.co.cu")

    fun getCaptcha() {
        viewModelScope.launch {
            try {
                _isLoadingCaptcha.postValue(true)
                _captchaImage.postValue(convertImageByteArrayToBitmap(service.getCaptcha()))
                _isLoadingCaptcha.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoadingCaptcha.postValue(false)
                _captchaLoadStatus.postValue(Pair(false, e.message))
            }
        }
    }

    fun login(userName: String, password: String, captchaCode: String) {
        viewModelScope.launch {
            try {
                val user = service.login(userName, password, captchaCode).toLocalUser(currentUser.value!!)
                _loginStatus.postValue(Pair(true, null))
                if (user.id != 0) {
                    repository.updateUserInRoom(user)
                } else {
                    repository.addUserToRoom(user)
                }
                _currentUser.postValue(repository.getUserFromRoom(_currentUser.value!!.id))
            } catch (e: Exception) {
                _loginStatus.postValue(Pair(false, e.message))
            }
        }
    }

    fun toUp(rechargeCode: String) {
        viewModelScope.launch {
            try {
                service.toUp(rechargeCode)
                _rechargeStatus.postValue(Pair(true, null))
            } catch (e: Exception) {
                e.printStackTrace()
                _rechargeStatus.postValue(Pair(false, e.message))
            }
        }
    }

    fun transfer(amount: Float, destinationAccount: String) {
        viewModelScope.launch {
            try {
                service.transfer(amount, destinationAccount)
                _transferStatus.postValue(Pair(true, null))
            } catch (e: Exception) {
                e.printStackTrace()
                _transferStatus.postValue(Pair(false, e.message))
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