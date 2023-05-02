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
import com.marlon.portalusuario.nauta.core.INITIAL_USER
import com.marlon.portalusuario.nauta.core.isValidCaptchaCode
import com.marlon.portalusuario.nauta.core.isValidPassword
import com.marlon.portalusuario.nauta.core.isValidUsername
import com.marlon.portalusuario.nauta.core.network.model.ResultType
import com.marlon.portalusuario.nauta.core.toPriceFloat
import com.marlon.portalusuario.nauta.core.toSeconds
import com.marlon.portalusuario.nauta.core.toTimeString
import com.marlon.portalusuario.nauta.data.network.NautaService
import com.marlon.portalusuario.nauta.data.network.UserRepository
import com.marlon.portalusuario.nauta.data.network.Users
import com.marlon.portalusuario.nauta.domain.AddUserUseCase
import com.marlon.portalusuario.nauta.domain.DeleteUserUseCase
import com.marlon.portalusuario.nauta.domain.UpdateUserUseCase
import com.marlon.portalusuario.nauta.domain.dto.UserDTO
import com.marlon.portalusuario.nauta.domain.model.UserModel
import com.marlon.portalusuario.nauta.service.CountdownServiceClient
import com.marlon.portalusuario.nauta.service.CountdownSubscriber
import cu.suitetecsa.sdk.nauta.core.exceptions.NotLoggedIn
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
    private val countdownServiceClient: CountdownServiceClient,
    private val addUserUseCase: AddUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel(), CountdownSubscriber {

    // Login section
    // Captcha
    private val _isReadyToUpdate = MutableLiveData<Boolean>()
    private val _isLoadingCaptcha = MutableLiveData<Boolean>()
    private val _captchaImage = MutableLiveData<Bitmap>()
    private val _captchaLoadStatus = MutableLiveData<Pair<Boolean, String?>>()

    val isReadyToUpdate: LiveData<Boolean> = _isReadyToUpdate
    val isLoadingCaptcha: LiveData<Boolean> = _isLoadingCaptcha
    val captchaImage: LiveData<Bitmap> = _captchaImage
    val captchaLoadStatus: LiveData<Pair<Boolean, String?>> = _captchaLoadStatus

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
    private val _rechargeCode = MutableLiveData<String>()
    private val _destinationAccount = MutableLiveData<TextFieldValue>()
    private val _amount = MutableLiveData<TextFieldValue>()
    private val _isEnabledRechargeButton = MutableLiveData<Boolean>()
    private val _isEnabledTransferButton = MutableLiveData<Boolean>()
    private val _loginStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _rechargeStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _transferStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _showCaptchaDialog = MutableLiveData<Pair<Boolean, () -> Unit>>()
    private val _users = MutableLiveData<Users>()
    private val _currentUser = MutableLiveData<UserModel>()
    private val _isChangingAccountAccessPassword = MutableLiveData<Boolean>()
    private val _accessAccountNewPassword = MutableLiveData<String>()
    private val _accessAccountChangePasswordStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _isChangingEmailPassword = MutableLiveData<Boolean>()
    private val _emailOldPassword = MutableLiveData<String>()
    private val _emailNewPassword = MutableLiveData<String>()
    private val _emailChangePasswordStatus = MutableLiveData<Pair<Boolean, String?>>()

    val isLogging: LiveData<Boolean> = _isLogging
    val isUpdatingUserInfo: LiveData<Boolean> = _isUpdatingUserInfo
    val isRecharging: LiveData<Boolean> = _isRecharging
    val rechargeCode: LiveData<String> = _rechargeCode
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
    val currentUser: LiveData<UserModel> = _currentUser
    val isChangingAccountAccessPassword: LiveData<Boolean> = _isChangingAccountAccessPassword
    val accessAccountNewPassword: LiveData<String> = _accessAccountNewPassword
    val accessAccountChangePasswordStatus: LiveData<Pair<Boolean, String?>> = _accessAccountChangePasswordStatus
    val isChangingEmailPassword: LiveData<Boolean> = _isChangingEmailPassword
    val emailOldPassword: LiveData<String> = _emailOldPassword
    val emailNewPassword: LiveData<String> = _emailNewPassword
    val emailChangePasswordStatus: LiveData<Pair<Boolean, String?>> = _emailChangePasswordStatus

    // QRScanner
    private val _showQRCodeScanner = MutableLiveData<Pair<Boolean, (String) -> Unit>>()
    val showQRCodeScanner: LiveData<Pair<Boolean, (String) -> Unit>> = _showQRCodeScanner

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
                                it.first { user -> user.id == pref.currentUserId }
                        } else _currentUser.value = it.last()
                    }
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
                    if (_leftTime.value!!.toSeconds() > pref.reservedTime) {
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
    fun onUserSelected(user: UserModel) {
        clearFields()
        _currentUser.postValue(user)
        if (user.id == 0) getCaptcha() else _leftTime.postValue(user.remainingTime.toTimeString())
    }

    fun addUser(userName: String, password: String, captchaCode: String) {
        clearFields()
        resetStatus()
        viewModelScope.launch {
            addUserUseCase(
                UserDTO(
                    id = 0,
                    username = userName,
                    password = password,
                    captchaCode = captchaCode
                )
            )
                .collect {
                    _isLogging.postValue(true)
                    _isLoading.postValue(true)
                    when (it) {
                        is ResultType.Error -> {
                            _isLogging.postValue(false)
                            _isLoading.postValue(false)
                            _loginStatus.postValue(Pair(false, it.message))
                        }

                        is ResultType.Success -> {
                            _isLogging.postValue(false)
                            _isLoading.postValue(false)
                            _loginStatus.postValue(Pair(true, null))
                            _currentUser.postValue(it.data!!)
                        }
                    }
                }
        }
    }

    fun updateUser(captchaCode: String? = null, postUpdate: () -> Unit) {
        updateUser(captchaCode)
        postUpdate()
    }

    fun updateUser(captchaCode: String? = null) {
        clearFields()
        resetStatus()
        viewModelScope.launch {
            updateUserUseCase(
                UserDTO(
                    id = _currentUser.value!!.id,
                    username = _currentUser.value!!.username,
                    password = _currentUser.value!!.password,
                    captchaCode = captchaCode
                )
            )
                .collect {
                    _isLogging.postValue(true)
                    _isLoading.postValue(true)
                    when (it) {
                        is ResultType.Error -> {
                            if (it.message == "Not logged in") {
                                showCaptchaDialog(true) {}
                                _isLogging.postValue(false)
                                _isLoading.postValue(false)
                            } else {
                                _isLogging.postValue(false)
                                _isLoading.postValue(false)
                                _loginStatus.postValue(Pair(false, it.message))
                            }
                        }

                        is ResultType.Success -> {
                            _isLogging.postValue(false)
                            _isLoading.postValue(false)
                            _loginStatus.postValue(Pair(true, null))
                            _currentUser.postValue(it.data!!)
                        }
                    }
                }
        }
    }

    fun deleteUser() {
        clearFields()
        resetStatus()
        viewModelScope.launch {
            deleteUserUseCase(
                UserDTO(
                    id = _currentUser.value!!.id, username = "", password = "", captchaCode = null
                )
            )
                .collect {
                    _isLoading.postValue(true)
                    when (it) {
                        is ResultType.Error -> {}
                        is ResultType.Success -> {
                            _users.postValue(it.data!!)
                            if (it.data.isNotEmpty()) {
                                _currentUser.postValue(it.data.last())
                            } else {
                                _currentUser.postValue(INITIAL_USER)
                            }
                        }
                    }
                    _isLoading.postValue(false)
                }
        }
    }

    // Data validate
    fun isValidDestinationAccount(destinationAccount: String) =
        (_currentUser.value!!.offer != null && destinationAccount.isEmpty()) || destinationAccount.isValidUsername()

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
        _isReadyToUpdate.postValue(captchaCode.isValidCaptchaCode())
        _captchaCode.value = captchaCode
    }

    fun onChangeRechargeCode(rechargeCode: String) {
        resetStatus()
        if (rechargeCode.length <= 16) {
            _rechargeCode.value = rechargeCode
        }
        _isEnabledRechargeButton.postValue(rechargeCode.length == 12 || rechargeCode.length == 16)
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
        _loginEnable.postValue(userName.text.isValidUsername() && password.isValidPassword() && captchaCode.isValidCaptchaCode())
    }

    fun onAccountAccessPasswordChange(password: String) {
        _accessAccountNewPassword.value = password
    }

    fun onEmailPasswordChange(oldPassword: String, newPassword: String) {
        _emailOldPassword.value = oldPassword
        _emailNewPassword.value = newPassword
    }

    fun onChangeLimiterTime(newLimiterTime: Pair<Int, Int>) {
        resetStatus()
        val (hour, minute) = newLimiterTime
        _limitedTime.postValue(String.format("%02d:%02d:00", hour, minute))
    }

    override fun onTimeLeftChanged(timeLeftInMillis: Long) {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (_currentUser.value!!.username == _userConnected.value!!) {
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
        _rechargeCode.postValue("")
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

    fun showQRCodeScanner(isShow: Boolean, postLoginAction: (String) -> Unit) {
        _showQRCodeScanner.postValue(Pair(isShow, postLoginAction))
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
                _userConnected.postValue(_currentUser.value!!.username)
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
                service.getRemainingTime { _currentUser.value!!.remainingTime = it.toSeconds() }

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
                service.getConnectInfo(_currentUser.value!!.username, _currentUser.value!!.password) {
                    _currentUser.value!!.credit = it.toPriceFloat()
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
                _rechargeCode.postValue("")
                updateUser()
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

    fun changePassword(newPassword: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isChangingAccountAccessPassword.postValue(true)
                _isLoading.postValue(true)
                if (pref.currentUserId == _currentUser.value!!.id) {
                    service.changePassword(newPassword)
                } else {
                    showCaptchaDialog(true) { changePassword(newPassword) }
                    getCaptcha()
                }
                _isChangingAccountAccessPassword.postValue(false)
                _isLoading.postValue(false)
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { changePassword(newPassword) }
                getCaptcha()
                _isChangingAccountAccessPassword.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _isChangingAccountAccessPassword.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    fun changeEmailPassword(oldPassword: String, newPassword: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isChangingEmailPassword.postValue(true)
                _isLoading.postValue(true)
                if (pref.currentUserId == _currentUser.value!!.id) {
                    service.changeEmailPassword(oldPassword, newPassword)
                } else {
                    showCaptchaDialog(true) { changeEmailPassword(oldPassword, newPassword) }
                    getCaptcha()
                }
                _isChangingEmailPassword.postValue(false)
                _isLoading.postValue(false)
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { changeEmailPassword(oldPassword, newPassword) }
                getCaptcha()
                _isChangingEmailPassword.postValue(false)
                _isLoading.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
                _isChangingEmailPassword.postValue(false)
                _isLoading.postValue(false)
            }
        }
    }

    private fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    fun generatePassword(isEmailPassword: Boolean) {
        resetStatus()
        if (isEmailPassword) _emailNewPassword.postValue(service.generatePassword()) else _accessAccountNewPassword.postValue(service.generatePassword())
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