package cu.suitetecsa.nautanav.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cu.suitetecsa.nautanav.util.INITIAL_USER
import cu.suitetecsa.nautanav.core.isValidCaptchaCode
import cu.suitetecsa.nautanav.core.isValidPassword
import cu.suitetecsa.nautanav.core.isValidUsername
import cu.suitetecsa.nautanav.core.network.model.ResultType
import cu.suitetecsa.nautanav.core.toPriceFloat
import cu.suitetecsa.nautanav.core.toSeconds
import cu.suitetecsa.nautanav.core.toTimeString
import cu.suitetecsa.nautanav.data.network.NautaService
import cu.suitetecsa.nautanav.data.network.UserRepository
import cu.suitetecsa.nautanav.data.network.Users
import cu.suitetecsa.nautanav.domain.AddUserUseCase
import cu.suitetecsa.nautanav.domain.DeleteUserUseCase
import cu.suitetecsa.nautanav.domain.UpdateUserUseCase
import cu.suitetecsa.nautanav.domain.dto.UserDTO
import cu.suitetecsa.nautanav.domain.model.UserModel
import cu.suitetecsa.nautanav.service.CountdownServiceClient
import cu.suitetecsa.nautanav.service.CountdownSubscriber
import cu.suitetecsa.nautanav.ui.components.connectview.ConnectViewState
import cu.suitetecsa.nautanav.util.Pref
import cu.suitetecsa.sdk.nauta.core.exceptions.NotLoggedIn
import cu.suitetecsa.sdk.nauta.domain.util.timeStringToSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    // ViewStates
    private val _connectViewState = MutableStateFlow<ConnectViewState>(
        ConnectViewState.Disconnected("00:00:00", {}, {})
    )
    val connectViewState: StateFlow<ConnectViewState> get() = _connectViewState.asStateFlow()

    // ValuesState
    private val _users = MutableStateFlow<Users>(listOf())
    val users: StateFlow<Users> = _users.asStateFlow()
    private val _userName = MutableStateFlow(TextFieldValue(""))
    val userName: StateFlow<TextFieldValue> = _userName.asStateFlow()
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

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
    private val _captchaCode = MutableLiveData<String>()
    private val _loginEnable = MutableLiveData<Boolean>()
    private val _isLoading = MutableLiveData<Boolean>()
    private val _isLoggedIn = MutableLiveData<Boolean>()
    private val _suggestion: MutableState<String> = mutableStateOf("")

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
    private val _currentUser = MutableStateFlow(INITIAL_USER)
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
    val currentUser: StateFlow<UserModel> = _currentUser.asStateFlow()
    val isChangingAccountAccessPassword: LiveData<Boolean> = _isChangingAccountAccessPassword
    val accessAccountNewPassword: LiveData<String> = _accessAccountNewPassword
    val accessAccountChangePasswordStatus: LiveData<Pair<Boolean, String?>> =
        _accessAccountChangePasswordStatus
    val isChangingEmailPassword: LiveData<Boolean> = _isChangingEmailPassword
    val emailOldPassword: LiveData<String> = _emailOldPassword
    val emailNewPassword: LiveData<String> = _emailNewPassword
    val emailChangePasswordStatus: LiveData<Pair<Boolean, String?>> = _emailChangePasswordStatus

    // Initialize UI
    init {
        viewModelScope.launch {
            // Subscribe to service
            countdownServiceClient.isBound.observeForever { isBound ->
                if (isBound) countdownServiceClient.serviceSubscribe(this@NautaViewModel)
            }

            // Initialize list of users
            repository.getUsersFromRoom { userList ->
                _users.value = userList
                if (userList.isNotEmpty()) {
                    if (pref.currentUserId != 0) {
                        _currentUser.value =
                            userList.firstOrNull { user -> user.id == pref.currentUserId }
                                ?: userList.last()
                    } else _currentUser.value = userList.last()
                    _connectViewState.value = ConnectViewState.Disconnected(
                        _currentUser.value.remainingTime.toTimeString(),
                        { showTimePickerDialog(true) },
                        ::connect
                    )
                }
            }
        }
    }

    fun onCreated() {
        viewModelScope.launch {
            // Check internet access
            val dataSession = pref.getSession()
            if (dataSession.isNotEmpty()) {
                service.setDataSession(dataSession)
                _isLoggedIn.value = true
                _connectViewState.value = ConnectViewState.Loading(_currentUser.value.remainingTime.toTimeString())
                try {
                    service.getRemainingTime().takeIf {  it != "errorop" }?.also {  timeResponse ->
                        _leftTime.value = timeResponse
                        _connectViewState.value = ConnectViewState.Connected(timeResponse, ::disconnect)
                        _userConnected.value = dataSession["username"]
                        if (_leftTime.value!!.toSeconds() > pref.reservedTime) {
                            countdownServiceClient.setParams(pref.reservedTime, service)
                            countdownServiceClient.serviceConnect()
                        } else {
                            disconnect()
                        }
                    } ?: run {
                        _connectViewState.value = ConnectViewState.Failure("errorop", "Olvidar") {
                            countdownServiceClient.serviceDisconnect()

                            // Removing data session
                            pref.removeSession(pref.getSession())
                            pref.reservedTime = 0
                            _connectViewState.value = ConnectViewState.Disconnected(
                                "errorop",
                                { showTimePickerDialog(true) },
                                ::connect
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _connectStatus.postValue(Pair(false, e.message))
                }
            }

            //PreLogin on user portal
            if (_currentUser.value == INITIAL_USER) {
                try {
                    _isLoadingCaptcha.value = true
                    _captchaImage.value = convertImageByteArrayToBitmap(service.getCaptcha())
                    _isLoadingCaptcha.value = false
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isLoadingCaptcha.value = false
                    _captchaLoadStatus.value = Pair(false, e.message)
                }
            }
        }
    }

    // User managing
    fun onUserSelected(user: UserModel) {
        clearFields()
        _currentUser.value = user
        if (user.id == 0) getCaptcha() else _leftTime.value = user.remainingTime.toTimeString()
    }

    fun addUser(userName: String, password: String, captchaCode: String) {
        resetStatus()
        viewModelScope.launch {
            _isLogging.value = true
            _isLoading.value = true
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
                            _isLogging.value = false
                            _isLoading.value = false
                            _loginStatus.value = Pair(false, it.message)
                        }

                        is ResultType.Success -> {
                            _isLogging.value = false
                            _isLoading.value = false
                            _loginStatus.value = Pair(true, null)
                            repository.getUsersFromRoom { users ->
                                _users.value = users

                                _currentUser.value = it.data
                                pref.currentUserId = it.data.id
                                _connectViewState.value = ConnectViewState.Disconnected(
                                    _currentUser.value.remainingTime.toTimeString(),
                                    { _showTimePickerDialog.value = true },
                                    ::connect
                                )
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
            _isLogging.value = true
            _isUpdatingUserInfo.value = true
            _isLoading.value = true
            if (_currentUser.value.id == pref.currentUserId) {
                updateUserUseCase(
                    UserDTO(
                        id = _currentUser.value.id,
                        username = _currentUser.value.username,
                        password = _currentUser.value.password,
                        captchaCode = captchaCode
                    )
                )
                    .collect {
                        when (it) {
                            is ResultType.Error -> {
                                if (it.message == "Not logged in") {
                                    showCaptchaDialog(true) {}
                                    _isLogging.value = false
                                    _isUpdatingUserInfo.value = false
                                    _isLoading.value = false
                                } else {
                                    _isLogging.value = false
                                    _isUpdatingUserInfo.value = false
                                    _isLoading.value = false
                                    _loginStatus.value = Pair(false, it.message)
                                    getCaptcha()
                                }
                            }

                            is ResultType.Success -> {
                                _isLogging.value = false
                                _isUpdatingUserInfo.value = false
                                _isLoading.value = false
                                _loginStatus.value = Pair(true, null)
                                repository.getUsersFromRoom { users ->
                                    _users.value = users

                                    _currentUser.value = it.data
                                    pref.currentUserId = _currentUser.value.id
                                }
                                postUpdate()
                            }
                        }
                    }
            } else {
                pref.currentUserId = _currentUser.value.id
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
                    id = _currentUser.value.id, username = "", password = "", captchaCode = null
                )
            )
                .collect {
                    _isLoading.postValue(true)
                    when (it) {
                        is ResultType.Error -> {}
                        is ResultType.Success -> {
                            _users.value = it.data
                            if (it.data.isNotEmpty()) {
                                _currentUser.value = it.data.last()
                            } else {
                                _currentUser.value = INITIAL_USER
                            }
                        }
                    }
                    _isLoading.value = false
                }
        }
    }

    // Data validate
    fun isValidDestinationAccount(destinationAccount: String) =
        (_currentUser.value.offer != null && destinationAccount.isEmpty()) || destinationAccount.isValidUsername()

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
        _isEnabledRechargeButton.value = rechargeCode.length == 12 || rechargeCode.length == 16
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
        _loginEnable.value = userName.text.isValidUsername() && password.isValidPassword() &&
                captchaCode.isValidCaptchaCode()
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
        _limitedTime.value = "%02d:%02d:00".format(hour, minute)
    }

    override fun onTimeLeftChanged(timeLeftInMillis: Long) {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (_currentUser.value.username == _userConnected.value!!) {
            if (reservedTime != 0) {
                _leftTime.value = ((timeLeftInMillis / 1000).toInt() + reservedTime).toTimeString()
            } else {
                _leftTime.value = ((timeLeftInMillis / 1000).toInt()).toTimeString()
            }
        }
        _connectViewState.value = ConnectViewState.Connected(
            ((timeLeftInMillis / 1000).toInt()).toTimeString(),
            ::disconnect
        )
        _limitedTime.value = ((timeLeftInMillis / 1000).toInt()).toTimeString()
    }

    override fun onTimerFinished() {
        resetStatus()
        val reservedTime = pref.reservedTime
        if (reservedTime != 0) {
            _leftTime.value = reservedTime.toTimeString()
        } else _leftTime.value = "00:00:00"
        _limitedTime.value = "00:00:00"
        _connectViewState.value = ConnectViewState.Disconnected(
            "00:00:00",
            { showTimePickerDialog(true) },
            ::connect
        )
        _isLoggedIn.value = false
    }

    private fun clearFields() {
        _userName.value = TextFieldValue("")
        _password.value = ""
        _captchaCode.value = ""
        _rechargeCode.value = ""
        _amount.value = TextFieldValue("")
        _destinationAccount.value = TextFieldValue("")
        _accessAccountNewPassword.value = ""
        _emailNewPassword.value = ""
        _emailOldPassword.value = ""
    }

    fun resetStatus() {
        _rechargeStatus.value = Pair(true, null)
        _transferStatus.value = Pair(true, null)
        _loginStatus.value = Pair(true, null)
        _connectStatus.value = Pair(true, null)
        _captchaLoadStatus.value = Pair(true, null)
        _emailChangePasswordStatus.value = Pair(true, null)
        _accessAccountChangePasswordStatus.value = Pair(true, null)
    }

    // Show/hide dialogs
    fun showCaptchaDialog(isShow: Boolean, postLoginAction: () -> Unit) {
        _showCaptchaDialog.value = Pair(isShow, postLoginAction)
    }

    fun showTimePickerDialog(isShow: Boolean) {
        _showTimePickerDialog.value = isShow
    }

    // Network tasks
    fun connect() {
        resetStatus()
        viewModelScope.launch {
            try {
                _isConnecting.value = true
                _isLoading.value = true
                _connectViewState.value = ConnectViewState.Loading(_currentUser.value.remainingTime.toTimeString())
                service.connect(_currentUser.value.username, _currentUser.value.password)
                _connectStatus.value = Pair(true, null)
                _userConnected.value = _currentUser.value.username
                pref.saveSession(service.getDataSession())
                pref.currentUserId = currentUser.value.id
                _isLoggedIn.value = true
                val timeResponse = service.getRemainingTime()
                _leftTime.value = timeResponse
                if (_limitedTime.value == null) {
                    _limitedTime.value = timeResponse
                }
                if (_limitedTime.value != _leftTime.value) {
                    pref.reservedTime =
                        timeStringToSeconds(_leftTime.value!!) - timeStringToSeconds(_limitedTime.value!!)
                } else pref.reservedTime = 0

                countdownServiceClient.setParams(pref.reservedTime, service)
                countdownServiceClient.serviceConnect()

                _isConnecting.value = false
                _isLoading.value = false
                _connectViewState.value = ConnectViewState.Connected(_limitedTime.value!!, ::disconnect)
            } catch (e: Exception) {
                e.printStackTrace()
                _connectStatus.value = Pair(false, e.message.toString())
                _isConnecting.value = false
                _isLoading.value = false
                _connectViewState.value = ConnectViewState.Failure(
                    e.message.toString(),
                    "OK"
                ) {
                    _connectViewState.value = ConnectViewState.Disconnected(
                        _currentUser.value.remainingTime.toTimeString(),
                        { _showTimePickerDialog.value = true },
                        ::connect
                    )
                }
            }
        }
    }

    fun disconnect() {
        resetStatus()
        viewModelScope.launch {
            try {
                // Updating remaining time
                service.getRemainingTime {
                    _currentUser.value.remainingTime = it.toSeconds()
                    _connectViewState.value = ConnectViewState.Loading(it)
                }

                // Disconnecting
                _isConnecting.value = true
                _isLoading.value = true
                service.disconnect()
                _connectStatus.value = Pair(true, null)
                _isLoggedIn.value = false
                _userConnected.value = ""
                countdownServiceClient.serviceDisconnect()

                // Removing data session
                pref.removeSession(pref.getSession())
                pref.reservedTime = 0

                // Updating credit
                service.getConnectInfo(
                    _currentUser.value.username,
                    _currentUser.value.password
                ) {
                    _currentUser.value.credit = it.toPriceFloat()
                }

                // Updating user
                repository.updateUserInRoom(_currentUser.value)

                _isConnecting.value = false
                _isLoading.value = false
                _connectViewState.value = ConnectViewState.Disconnected(
                    _currentUser.value.remainingTime.toTimeString(),
                    { _showTimePickerDialog.value = true },
                    ::connect
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _connectStatus.value = Pair(false, e.message)
                _isConnecting.value = false
                _isLoading.value = false
            }
        }
    }

    fun getCaptcha() {
        resetStatus()
        viewModelScope.launch {
            try {
                _isLoadingCaptcha.value = true
                _isLoading.value = true
                _captchaImage.value = convertImageByteArrayToBitmap(service.getCaptcha())
                _isLoadingCaptcha.value = false
                _isLoading.value = false
                _captchaLoadStatus.value = Pair(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoadingCaptcha.value = false
                _captchaLoadStatus.value = Pair(false, e.message)
            }
        }
    }

    fun toUp(rechargeCode: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isRecharging.value = true
                _isLoading.value = true
                if (pref.currentUserId == _currentUser.value.id) {
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
                _rechargeStatus.value = Pair(true, null)
                _isRecharging.value = false
                _isLoading.value = false
                _rechargeCode.value = ""
                updateUser()
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { toUp(rechargeCode) }
                getCaptcha()
            } catch (e: Exception) {
                e.printStackTrace()
                _isRecharging.value = false
                _isLoading.value = false
                _rechargeStatus.value = Pair(false, e.message)
            }
        }
    }

    fun transfer(amount: Float, destinationAccount: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isTransferring.value = true
                _isLoading.value = true
                if (pref.currentUserId == _currentUser.value.id) {
                    if (destinationAccount == "") service.payQuote(amount)
                    else service.transfer(amount, destinationAccount)
                    clearFields()
                } else {
                    showCaptchaDialog(true) { transfer(amount, destinationAccount) }
                    getCaptcha()
                }
                _transferStatus.value = Pair(true, null)
                _isTransferring.value = false
                _isLoading.value = false
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { transfer(amount, destinationAccount) }
                getCaptcha()
                _transferStatus.value = Pair(true, null)
                _isTransferring.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _isTransferring.value = false
                _isLoading.value = false
                _transferStatus.value = Pair(false, e.message)
            }
        }
    }

    fun changePassword(newPassword: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isChangingAccountAccessPassword.value = true
                _isLoading.value = true
                if (pref.currentUserId == _currentUser.value.id) {
                    service.changePassword(newPassword)
                    _currentUser.value.password = newPassword
                    updateUser()
                    clearFields()
                } else {
                    showCaptchaDialog(true) { changePassword(newPassword) }
                    getCaptcha()
                }
                _isChangingAccountAccessPassword.value = false
                _isLoading.value = false
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { changePassword(newPassword) }
                getCaptcha()
                _isChangingAccountAccessPassword.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _isChangingAccountAccessPassword.value = false
                _isLoading.value = false
            }
        }
    }

    fun changeEmailPassword(oldPassword: String, newPassword: String) {
        resetStatus()
        viewModelScope.launch {
            try {
                _isChangingEmailPassword.value = true
                _isLoading.value = true
                if (pref.currentUserId == _currentUser.value.id) {
                    service.changeEmailPassword(oldPassword, newPassword)
                    clearFields()
                } else {
                    showCaptchaDialog(true) { changeEmailPassword(oldPassword, newPassword) }
                    getCaptcha()
                }
                _isChangingEmailPassword.value = false
                _isLoading.value = false
            } catch (e: NotLoggedIn) {
                e.printStackTrace()
                showCaptchaDialog(true) { changeEmailPassword(oldPassword, newPassword) }
                getCaptcha()
                _isChangingEmailPassword.value = false
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _isChangingEmailPassword.value = false
                _isLoading.value = false
            }
        }
    }

    private fun convertImageByteArrayToBitmap(imageData: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    }

    fun generatePassword(isEmailPassword: Boolean) {
        resetStatus()
        if (isEmailPassword) _emailNewPassword.value = service.generatePassword()
        else _accessAccountNewPassword.value = service.generatePassword()
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