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
import com.marlon.portalusuario.nauta.ui.components.captchadialog.CaptchaDialogState
import com.marlon.portalusuario.nauta.ui.components.captchaview.CaptchaViewState
import com.marlon.portalusuario.nauta.ui.components.connectview.ConnectViewState
import com.marlon.portalusuario.util.Pref
import cu.suitetecsa.sdk.nauta.core.exceptions.NotLoggedIn
import cu.suitetecsa.sdk.nauta.domain.util.timeStringToSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import trikita.log.Log
import javax.inject.Inject

private const val TAG = "NautaViewModel"

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
    val showTimePickerDialog: LiveData<Boolean> = _showTimePickerDialog

    // User
    private val _isRunningSomeTask = MutableLiveData<Boolean>()
    val isRunningSomeTask: LiveData<Boolean> = _isRunningSomeTask

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
    private val _users = MutableLiveData<Users>()
    private val _currentUser = MutableLiveData<UserModel>()
    private val _isChangingAccountAccessPassword = MutableLiveData<Boolean>()
    private val _accessAccountNewPassword = MutableLiveData<String>()
    private val _accessAccountChangePasswordStatus = MutableLiveData<Pair<Boolean, String?>>()
    private val _isChangingEmailPassword = MutableLiveData<Boolean>()
    private val _emailOldPassword = MutableLiveData<String>()
    private val _emailNewPassword = MutableLiveData<String>()
    private val _emailChangePasswordStatus = MutableLiveData<Pair<Boolean, String?>>()

    // New
    private val _captchaDialogState = MutableLiveData<CaptchaDialogState>(CaptchaDialogState.Hidden)
    val captchaDialogState: LiveData<CaptchaDialogState> = _captchaDialogState

    private val _captchaViewState = MutableLiveData<CaptchaViewState>(CaptchaViewState.Loading)
    val captchaViewState: LiveData<CaptchaViewState> = _captchaViewState

    private val _connectViewState = MutableLiveData<ConnectViewState>(ConnectViewState.Disconnected)
    val connectViewState: LiveData<ConnectViewState> = _connectViewState

    private val _postUpdateAction = MutableLiveData {}
    val postUpdateAction: LiveData<() -> Unit> = _postUpdateAction

    val isUpdatingUserInfo: LiveData<Boolean> = _isUpdatingUserInfo
    val isRecharging: LiveData<Boolean> = _isRecharging
    val rechargeCode: LiveData<String> = _rechargeCode
    val destinationAccount: LiveData<TextFieldValue> = _destinationAccount
    val amount: LiveData<TextFieldValue> = _amount
    val isEnabledRechargeButton: LiveData<Boolean> = _isEnabledRechargeButton
    val isEnabledTransferButton: LiveData<Boolean> = _isEnabledTransferButton
    val isTransferring: LiveData<Boolean> = _isTransferring
    val rechargeStatus: LiveData<Pair<Boolean, String?>> = _rechargeStatus
    val transferStatus: LiveData<Pair<Boolean, String?>> = _transferStatus
    val users: LiveData<Users> = _users
    val currentUser: LiveData<UserModel> = _currentUser
    val isChangingAccountAccessPassword: LiveData<Boolean> = _isChangingAccountAccessPassword
    val accessAccountNewPassword: LiveData<String> = _accessAccountNewPassword
    val accessAccountChangePasswordStatus: LiveData<Pair<Boolean, String?>> =
        _accessAccountChangePasswordStatus
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

    fun recoverSession() {
        viewModelScope.launch {
            // Check internet access
            val dataSession = pref.getSession()
            if (dataSession.isNotEmpty()) {
                service.setDataSession(dataSession)
                _isLoggedIn.value = true
                try {
                    service.getRemainingTime {
                        if (it == "errorop") {
                            _connectViewState.value = ConnectViewState.FailConnectStatus(it)
                            return@getRemainingTime
                        } else {
                            _leftTime.value = it
                        }
                    }
                    _userConnected.value = dataSession["username"]
                    if (_leftTime.value!!.toSeconds() > pref.reservedTime) {
                        countdownServiceClient.setParams(pref.reservedTime, service)
                        countdownServiceClient.serviceConnect()
                        _connectViewState.value = ConnectViewState.Connected
                    } else {
                        disconnect()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _connectViewState.value = ConnectViewState
                        .FailConnectStatus(reason = e.message ?: "Some Error")
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

    fun forgotSession() {
        pref.saveSession(emptyMap())
        _connectViewState.value = ConnectViewState.Disconnected
    }

    // User managing
    fun onUserSelected(user: UserModel) {
        clearFields()
        _currentUser.postValue(user)
        if (user.id == 0) getCaptcha() else _leftTime.postValue(user.remainingTime.toTimeString())
    }

    fun addUser(userName: String, password: String, captchaCode: String) {
        resetStatus()
        viewModelScope.launch {
            _isRunningSomeTask.postValue(true)
            _isLoading.postValue(true)
            addUserUseCase(
                UserDTO(
                    id = 0,
                    username = userName,
                    password = password,
                    captchaCode = captchaCode
                )
            )
                .collect {
                    when (it) {
                        is ResultType.Error -> {
                            _isRunningSomeTask.postValue(false)
                            _isLoading.postValue(false)
                            _loginStatus.postValue(Pair(false, it.message))
                        }

                        is ResultType.Success -> {
                            _isRunningSomeTask.postValue(false)
                            _isLoading.postValue(false)
                            _loginStatus.postValue(Pair(true, null))
                            repository.getUsersFromRoom { users ->
                                _users.value = users

                                _currentUser.value = it.data!!
                                pref.currentUserId = it.data!!.id
                            }
                            clearFields()
                        }
                    }
                }
        }
    }

    fun updateUser(captchaCode: String? = null) {
        updateUser(captchaCode) {}
    }

    fun updateUser(captchaCode: String? = null, postUpdate: () -> Unit) {
        clearFields()
        resetStatus()
        viewModelScope.launch {
            _isRunningSomeTask.postValue(true)
            _isUpdatingUserInfo.postValue(true)
            _isLoading.postValue(true)
            if (_currentUser.value!!.id == pref.currentUserId) {
                updateUserUseCase(
                    UserDTO(
                        id = _currentUser.value!!.id,
                        username = _currentUser.value!!.username,
                        password = _currentUser.value!!.password,
                        captchaCode = captchaCode
                    )
                )
                    .collect {
                        when (it) {
                            is ResultType.Error -> {
                                if (it.message == "Not logged in") {
                                    showCaptchaDialog(true) {}
                                    _isRunningSomeTask.postValue(false)
                                    _isUpdatingUserInfo.postValue(false)
                                    _isLoading.postValue(false)
                                } else {
                                    _isRunningSomeTask.postValue(false)
                                    _isUpdatingUserInfo.postValue(false)
                                    _isLoading.postValue(false)
                                    _loginStatus.postValue(Pair(false, it.message))
                                    getCaptcha()
                                }
                            }

                            is ResultType.Success -> {
                                _isRunningSomeTask.postValue(false)
                                _isUpdatingUserInfo.postValue(false)
                                _isLoading.postValue(false)
                                _loginStatus.postValue(Pair(true, null))
                                repository.getUsersFromRoom { users ->
                                    _users.value = users

                                    _currentUser.postValue(it.data!!)
                                    pref.currentUserId = _currentUser.value!!.id
                                }
                                postUpdate()
                            }
                        }
                    }
            } else {
                pref.currentUserId = _currentUser.value!!.id
                showCaptchaDialog(true) {}
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
        _isReadyToUpdate.value = captchaCode.isValidCaptchaCode()
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
        _destinationAccount.value = destinationAccount
        _amount.value = amount
    }

    fun onLoginChanged(userName: TextFieldValue, password: String, captchaCode: String) {

        resetStatus()

        onChangeUserName(userName)
        onChangePassword(password)
        onChangeCaptchaCode(captchaCode)
        _loginEnable.postValue(
            userName.text.isValidUsername() &&
                    password.isValidPassword() &&
                    captchaCode.isValidCaptchaCode()
        )
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
        _limitedTime.value = String.format("%02d:%02d:00", hour, minute)
    }

    override fun onTimeLeftChanged(timeLeftInMillis: Long) {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (_currentUser.value!!.username == _userConnected.value!!) {
            if (reservedTime != 0) {
                _leftTime.value = ((timeLeftInMillis / 1000).toInt() + reservedTime).toTimeString()
            } else {
                _leftTime.value = ((timeLeftInMillis / 1000).toInt()).toTimeString()
            }
        }
        _limitedTime.value = ((timeLeftInMillis / 1000).toInt()).toTimeString()
    }

    override fun onTimerFinished() {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (reservedTime != 0) {
            _leftTime.value = reservedTime.toTimeString()
        } else _leftTime.value = "00:00:00"
        _limitedTime.value = "00:00:00"
        _isLoggedIn.value = false
    }

    private fun clearFields() {
        _userName.postValue(TextFieldValue(""))
        _password.postValue("")
        _captchaCode.postValue("")
        _rechargeCode.postValue("")
        _amount.postValue(TextFieldValue(""))
        _destinationAccount.postValue(TextFieldValue(""))
        _accessAccountNewPassword.postValue("")
        _emailNewPassword.postValue("")
        _emailOldPassword.postValue("")
    }

    private fun resetStatus() {
        _rechargeStatus.postValue(Pair(true, null))
        _transferStatus.postValue(Pair(true, null))
        _loginStatus.postValue(Pair(true, null))
        _connectStatus.postValue(Pair(true, null))
        _captchaLoadStatus.postValue(Pair(true, null))
        _emailChangePasswordStatus.postValue(Pair(true, null))
        _accessAccountChangePasswordStatus.postValue(Pair(true, null))
    }

    // Show/hide dialogs
    fun showCaptchaDialog(isShow: Boolean, postLoginAction: () -> Unit) {
        _postUpdateAction.value = postLoginAction
        _captchaDialogState.value =
            if (isShow) CaptchaDialogState.Showing() else CaptchaDialogState.Hidden
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
                service.getConnectInfo(
                    _currentUser.value!!.username,
                    _currentUser.value!!.password
                ) {
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
                _captchaViewState.value = CaptchaViewState.Loading
                _captchaViewState.value = CaptchaViewState.Success(
                    image = convertImageByteArrayToBitmap(service.getCaptcha())
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _captchaViewState.value = CaptchaViewState.Failure(
                    reason = e.message ?: "Some Error"
                )
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
                    Log.i(TAG, "Trying to top up the account balance")
                    service.toUp(rechargeCode)
                    Log.i(TAG, "Account balance recharged")
                    clearFields()
                } else {
                    Log.i(TAG, "New account to manage")
                    Log.i(TAG, "Showing captcha dialog")
                    showCaptchaDialog(true) { toUp(rechargeCode) }
                    Log.i(TAG, "Getting captcha image")
                    getCaptcha()
                    Log.i(TAG, "Captcha image loaded")
                }
                _rechargeStatus.postValue(Pair(true, null))
                _isRecharging.postValue(false)
                _isLoading.postValue(false)
                _rechargeCode.postValue("")
                updateUser()
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { toUp(rechargeCode) }
                getCaptcha()
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
                    clearFields()
                } else {
                    showCaptchaDialog(true) { transfer(amount, destinationAccount) }
                    getCaptcha()
                }
                _transferStatus.postValue(Pair(true, null))
                _isTransferring.postValue(false)
                _isLoading.postValue(false)
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
                    _currentUser.value!!.password = newPassword
                    updateUser()
                    clearFields()
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
                    clearFields()
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
        if (isEmailPassword) _emailNewPassword.postValue(service.generatePassword())
        else _accessAccountNewPassword.postValue(service.generatePassword())
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
