package com.marlon.portalusuario.nauta.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marlon.portalusuario.Pref
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.fullUserName
import com.marlon.portalusuario.commons.toLocalUser
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.data.network.NautaService
import com.marlon.portalusuario.nauta.data.repository.UserRepository
import com.marlon.portalusuario.nauta.data.repository.Users
import com.marlon.portalusuario.nauta.service.CountdownServiceClient
import com.marlon.portalusuario.nauta.service.CountdownSubscriber
import cu.suitetecsa.sdk.nauta.core.exceptions.NotLoggedIn
import cu.suitetecsa.sdk.nauta.domain.util.priceStringToFloat
import cu.suitetecsa.sdk.nauta.domain.util.secondsToTimeString
import cu.suitetecsa.sdk.nauta.domain.util.timeStringToSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NautaViewModel @Inject constructor(
    private val service: NautaService,
    private val repository: UserRepository,
    private val pref: Pref,
    private val countdownServiceClient: CountdownServiceClient
) : ViewModel(), CountdownSubscriber {

    // Captcha
    private val _isReadyToUpdate = MutableLiveData<Boolean>()
    private val _isLoadingCaptcha = MutableLiveData<Boolean>()
    private val _captchaImage = MutableLiveData<Bitmap>()
    private val _captchaLoadStatus = MutableLiveData<Pair<Boolean, String?>>()

    val isReadyToUpdate: LiveData<Boolean> = _isReadyToUpdate
    val isLoadingCaptcha: LiveData<Boolean> = _isLoadingCaptcha
    val captchaImage: LiveData<Bitmap> = _captchaImage
    val captchaLoadStatus: LiveData<Pair<Boolean, String?>> = _captchaLoadStatus

    // Connect
    private val _userConnected = MutableLiveData<String>()
    private val _leftTime = MutableLiveData<String>()
    private val _limitedTime = MutableLiveData<String>()
    private val _isConnecting = MutableLiveData<Boolean>()
    private val _connectStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _showTimePickerDialog = MutableLiveData<Boolean>()

    val userConnected: LiveData<String> = _userConnected
    val leftTime: LiveData<String> = _leftTime
    val limitedTime: LiveData<String> = _limitedTime
    val isConnecting: LiveData<Boolean> = _isConnecting
    val connectStatus: LiveData<Pair<Boolean, String?>> = _connectStatus
    val showTimePickerDialog: LiveData<Boolean> = _showTimePickerDialog

    // User
    private val _isLogging = MutableLiveData<Boolean>()
    private val _isUpdatingUserInfo = MutableLiveData<Boolean>()
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
    private val _showCaptchaDialog = MutableLiveData<Pair<Boolean, () -> Unit>>()
    private val _users = MutableLiveData<Users>()
    private val _currentUser = MutableLiveData<User>()

    val isLogging: LiveData<Boolean> = _isLogging
    val isUpdatingUserInfo: LiveData<Boolean> = _isUpdatingUserInfo
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
    val showCaptchaDialog: LiveData<Pair<Boolean, () -> Unit>> = _showCaptchaDialog
    val users: LiveData<Users> = _users
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

    // Initialize UI
    init {
        viewModelScope.launch {
            // Subscribe to service
            countdownServiceClient.isBound.observeForever {
                if (it) countdownServiceClient.serviceSubscribe(this@NautaViewModel)
            }

            // Initialize list of users
            if (_users.value.isNullOrEmpty()) {
                repository.getUsersFromRoom {
                    _users.value = it
                    if (it.isNotEmpty()) {
                        if (pref.currentUserId != 0) {
                            _currentUser.value =
                                it.filter { user -> user.id == pref.currentUserId }[0]
                        } else _currentUser.value = it.last()
                    }
                }

                if (!_users.value.isNullOrEmpty()) {
                    if (pref.currentUserId != 0) _currentUser.value =
                        repository.getUserFromRoom(pref.currentUserId)
                    else _currentUser.value = _users.value!!.last()
                }
            } else _currentUser.value = _users.value!!.last()
        }
    }

    fun onCreated() {
        viewModelScope.launch {
            // Check internet access
            val dataSession = pref.getSession()
            if (dataSession.isNotEmpty()) {
                service.setDataSession(dataSession)
                _isLoggedIn.postValue(true)
                try {
                    service.getRemainingTime { _leftTime.postValue(it) }
                    _userConnected.postValue(dataSession["username"])
                    if (timeStringToSeconds(_leftTime.value!!) > pref.reservedTime) {
                        countdownServiceClient.setParams(pref.reservedTime, service)
                        countdownServiceClient.serviceConnect()
                    } else {
                        disconnect()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _connectStatus.postValue(Pair(false, e.message))
                }
            }

            //PreLogin on user portal
            if (_currentUser.value == null) {
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

    // User managing
    fun onUserSelected(user: User) {
        clearFields()
        _currentUser.postValue(user)
        _leftTime.postValue(user.time)
        if (user.id == 0) getCaptcha()
    }

    private fun createUser(userName: String, password: String): User {
        return User(
            userName = userName.split("@")[0],
            password = password,
            accountNavigationType = if (userName.split("@")[1] == "nauta.com.cu") NavigationType.INTERNATIONAL else NavigationType.NATIONAL,
            lastConnection = 0L,
            blockingDate = "",
            dateOfElimination = "",
            accountType = "",
            serviceType = "",
            credit = "",
            time = "",
            mailAccount = ""
        )
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.deleteUserFromRoom(user)
            if (user.id == pref.currentUserId) pref.currentUserId = 0
            repository.getUsersFromRoom {
                _users.postValue(it)
                if (it.isNotEmpty()) {
                    _currentUser.postValue(_users.value!!.last())
                } else {
                    _currentUser.postValue(
                        User(
                            userName = "Agrega un usuario",
                            password = "",
                            accountNavigationType = NavigationType.INTERNATIONAL,
                            lastConnection = 0L,
                            blockingDate = "",
                            dateOfElimination = "",
                            accountType = "",
                            serviceType = "",
                            credit = "",
                            time = "",
                            mailAccount = ""
                        )
                    )
                }
            }
        }
    }

    // Data validate
    private fun isValidUserName(userName: String): Boolean =
        userName.endsWith("@nauta.com.cu") || userName.endsWith("@nauta.co.cu")

    private fun isValidPassword(password: String): Boolean = password.length in 8..16

    private fun isValidCaptchaCode(captchaCode: String): Boolean = captchaCode.length in 4..10

    fun isValidRechargePassword(rechargeCode: String) =
        rechargeCode.replace(" ", "").length in listOf(12, 16)

    fun isValidTransferAmount(amount: String) =
        amount != "0" && amount != "" && amount.toFloat() <= priceStringToFloat(_currentUser.value!!.credit)

    fun isValidDestinationAccount(destinationAccount: String) =
        (_currentUser.value!!.offer != null && destinationAccount.isEmpty()) || isValidUserName(destinationAccount)

    // Manage data
    private fun onChangeUserName(userName: TextFieldValue) {
        resetStatus()
        val internationalSuggestion = "@nauta.com.cu"
        val nationalSuggestion = "@nauta.co.cu"

        if (userName.text.isNotEmpty()) {
            val textInput = userName.text.substring(0 until userName.selection.start)
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
                text = "$textInput${_suggestion.value}", selection = TextRange(inputLength)
            )
        } else {
            _userName.value = userName
        }
    }

    private fun onChangePassword(password: String) {
        resetStatus()
        _password.value = password
    }

    fun onChangeCaptchaCode(captchaCode: String) {
        resetStatus()
        _isReadyToUpdate.postValue(isValidCaptchaCode(captchaCode))
        _captchaCode.value = captchaCode
    }

    fun onChangeRechargeCode(rechargeCode: TextFieldValue) {
        resetStatus()
        if (rechargeCode.text.length <= 16) {
            _rechargeCode.postValue(rechargeCode)
        }
        _isEnabledRechargeButton.postValue(rechargeCode.text.length == 12 || rechargeCode.text.length == 16)
    }

    fun onTransferChange(destinationAccount: TextFieldValue, amount: TextFieldValue) {
        resetStatus()
        _destinationAccount.postValue(destinationAccount)
        _amount.postValue(amount)
    }

    fun onLoginChanged(userName: TextFieldValue, password: String, captchaCode: String) {

        resetStatus()

        onChangeUserName(userName)
        onChangePassword(password)
        onChangeCaptchaCode(captchaCode)
        _loginEnable.postValue(
            isValidUserName(userName.text) &&
                    isValidPassword(password) &&
                    isValidCaptchaCode(captchaCode)
        )
    }

    fun onChangeLimiterTime(newLimiterTime: Pair<Int, Int>) {
        resetStatus()
        val (hour, minute) = newLimiterTime
        _limitedTime.postValue(String.format("%02d:%02d:00", hour, minute))
    }

    override fun onTimeLeftChanged(timeLeftInMillis: Long) {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (_currentUser.value!!.fullUserName() == _userConnected.value!!) {
            if (reservedTime != 0) {
                _leftTime.postValue(secondsToTimeString((timeLeftInMillis / 1000).toInt() + reservedTime))
            } else {
                _leftTime.postValue(secondsToTimeString((timeLeftInMillis / 1000).toInt()))
            }
        }
        _limitedTime.postValue(secondsToTimeString((timeLeftInMillis / 1000).toInt()))
    }

    override fun onTimerFinished() {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (reservedTime != 0) {
            _leftTime.postValue(secondsToTimeString(reservedTime))
        } else _leftTime.postValue("00:00:00")
        _limitedTime.postValue("00:00:00")
        _isLoggedIn.postValue(false)
    }

    private fun clearFields() {
        _userName.postValue(TextFieldValue(""))
        _password.postValue("")
        _captchaCode.postValue("")
        _rechargeCode.postValue(TextFieldValue(""))
        _amount.postValue(TextFieldValue(""))
        _destinationAccount.postValue(TextFieldValue(""))
    }

    fun resetStatus() {
        _rechargeStatus.postValue(Pair(true, null))
        _transferStatus.postValue(Pair(true, null))
        _loginStatus.postValue(Pair(true, null))
        _connectStatus.postValue(Pair(true, null))
        _captchaLoadStatus.postValue(Pair(true, null))
    }

    // Show/hide dialogs
    fun showCaptchaDialog(isShow: Boolean, postLoginAction: () -> Unit) {
        _showCaptchaDialog.postValue(Pair(isShow, postLoginAction))
    }

    fun showTimePickerDialog(isShow: Boolean) {
        _showTimePickerDialog.postValue(isShow)
    }

    // Network tasks
    fun connect(userName: String, password: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isConnecting.postValue(true)
                _isLoading.postValue(true)
                service.connect(userName, password)
                _connectStatus.postValue(Pair(true, null))
                _userConnected.postValue(_currentUser.value!!.fullUserName())
                pref.saveSession(service.getDataSession())
                pref.currentUserId = currentUser.value!!.id
                _isLoggedIn.postValue(true)
                service.getRemainingTime {
                    _leftTime.postValue(it)
                    if (_limitedTime.value == null) {
                        _limitedTime.postValue(it)
                    }
                }
                println(_limitedTime.value)
                if (_limitedTime.value != _leftTime.value) {
                    pref.reservedTime =
                        timeStringToSeconds(_leftTime.value!!) - timeStringToSeconds(_limitedTime.value!!)
                } else pref.reservedTime = 0

                countdownServiceClient.setParams(pref.reservedTime, service)
                countdownServiceClient.serviceConnect()

                _isConnecting.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _connectStatus.postValue(Pair(false, e.message.toString()))
                _isConnecting.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    fun disconnect() {
        resetStatus()
        viewModelScope.launch {
            try {
                // Updating remaining time
                service.getRemainingTime { _currentUser.value!!.credit = it }

                // Disconnecting
                _isConnecting.postValue(true)
                _isLoading.postValue(true)
                service.disconnect()
                _connectStatus.postValue(Pair(true, null))
                _isLoggedIn.postValue(false)
                _userConnected.postValue("")
                countdownServiceClient.serviceDisconnect()

                // Removing data session
                pref.removeSession(pref.getSession())
                pref.reservedTime = 0

                // Updating credit
                service.getConnectInfo {
                    _currentUser.value!!.credit = it
                }

                // Updating user
                repository.updateUserInRoom(_currentUser.value!!)

                _isConnecting.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _connectStatus.postValue(Pair(false, e.message))
                _isConnecting.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    fun getCaptcha() {
        resetStatus()
        viewModelScope.launch {
            try {
                _isLoadingCaptcha.postValue(true)
                _isLoading.postValue(true)
                _captchaImage.postValue(convertImageByteArrayToBitmap(service.getCaptcha()))
                _isLoadingCaptcha.postValue(false)
                _isLoading.postValue(false)
                _captchaLoadStatus.postValue(Pair(true, null))
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoadingCaptcha.postValue(false)
                _captchaLoadStatus.postValue(Pair(false, e.message))
            }
        }
    }

    fun login(
        userName: String,
        password: String,
        captchaCode: String,
        postLoginAction: () -> Unit
    ) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isLogging.postValue(true)
                _isLoading.postValue(true)
                val cUser =
                    if (_currentUser.value == null || _currentUser.value!!.id == 0) createUser(
                        userName, password
                    ) else _currentUser.value!!
                val user = service.login(userName, password, captchaCode).toLocalUser(cUser)
                if (user.id != 0) {
                    repository.updateUserInRoom(user)
                    repository.getUsersFromRoom { users ->
                        _users.postValue(users)
                        _currentUser.postValue(users.filter { it.id == _currentUser.value!!.id }[0])
                        pref.currentUserId = _currentUser.value!!.id
                    }
                } else {
                    repository.addUserToRoom(user)
                    repository.getUsersFromRoom { users ->
                        _users.postValue(users)
                        _currentUser.postValue(users.last())
                        pref.currentUserId = users.last().id
                    }
                }
                _isLogging.postValue(false)
                _isLoading.postValue(false)
                _loginStatus.postValue(Pair(true, null))
                _userName.postValue(TextFieldValue(""))
                _password.postValue("")
                _captchaCode.postValue("")
                postLoginAction()
            } catch (e: Exception) {
                _isLogging.postValue(false)
                _isLoading.postValue(false)
                _loginStatus.postValue(Pair(false, e.message))
                _captchaCode.postValue("")
                getCaptcha()
            }
        }
    }

    fun updateUserInformation() {
        resetStatus()
        viewModelScope.launch {
            try {
                _isUpdatingUserInfo.postValue(true)
                _isLoading.postValue(true)
                if (pref.currentUserId == _currentUser.value!!.id) {
                    val nautaUser = service.userInformation()
                    val localUser = nautaUser.toLocalUser(_currentUser.value!!)
                    repository.updateUserInRoom(localUser)
                    _currentUser.postValue(localUser)
                } else {
                    showCaptchaDialog(true) {}
                    getCaptcha()
                }
                _isUpdatingUserInfo.postValue(false)
                _isLoading.postValue(false)
            } catch (e: NotLoggedIn) {
                showCaptchaDialog(true) {}
                getCaptcha()
                e.printStackTrace()
                _isUpdatingUserInfo.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _isUpdatingUserInfo.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    fun toUp(rechargeCode: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isRecharging.postValue(true)
                _isLoading.postValue(true)
                if (pref.currentUserId == _currentUser.value!!.id) {
                    service.toUp(rechargeCode)
                } else {
                    showCaptchaDialog(true) { toUp(rechargeCode) }
                    getCaptcha()
                }
                _rechargeStatus.postValue(Pair(true, null))
                _isRecharging.postValue(false)
                _isLoading.postValue(false)
                _rechargeCode.postValue(TextFieldValue(""))
                updateUserInformation()
            } catch (e: NotLoggedIn) {
                showCaptchaDialog(true) { toUp(rechargeCode) }
                getCaptcha()
                e.printStackTrace()
                _rechargeStatus.postValue(Pair(true, null))
                _isRecharging.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _isRecharging.postValue(false)
                _isLoading.postValue(false)
                _rechargeStatus.postValue(Pair(false, e.message))
            }
        }
    }

    fun transfer(amount: Float, destinationAccount: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isTransferring.postValue(true)
                _isLoading.postValue(true)
                if (pref.currentUserId == _currentUser.value!!.id) {
                    if (destinationAccount == "") service.payQuote(amount)
                    else service.transfer(amount, destinationAccount)
                } else {
                    showCaptchaDialog(true) { transfer(amount, destinationAccount) }
                    getCaptcha()
                }
                _transferStatus.postValue(Pair(true, null))
                _isTransferring.postValue(false)
                _isLoading.postValue(false)
                _amount.postValue(TextFieldValue(""))
                _destinationAccount.postValue(TextFieldValue(""))
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { transfer(amount, destinationAccount) }
                getCaptcha()
                _transferStatus.postValue(Pair(true, null))
                _isTransferring.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _isTransferring.postValue(false)
                _isLoading.postValue(false)
                _transferStatus.postValue(Pair(false, e.message))
            }
        }
    }

    private fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    override fun onCleared() {
        countdownServiceClient.isBound.removeObserver { }
        countdownServiceClient.isBound.value?.let {
            if (it) {
                countdownServiceClient.serviceUnsubscribe(this)
                countdownServiceClient.serviceDisconnect()
                countdownServiceClient.isBound.value?.let { isBound ->
                    if (isBound) countdownServiceClient.serviceSubscribe(this)
                }
            }
        }
        super.onCleared()
    }
}