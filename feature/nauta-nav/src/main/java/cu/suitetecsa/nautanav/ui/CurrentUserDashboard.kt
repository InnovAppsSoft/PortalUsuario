package cu.suitetecsa.nautanav.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cu.suitetecsa.nauta_nav.R
import cu.suitetecsa.nautanav.core.isValidAmountToTransfer
import cu.suitetecsa.nautanav.core.isValidPassword
import cu.suitetecsa.nautanav.core.isValidRechargeCode
import cu.suitetecsa.nautanav.core.toSeconds
import cu.suitetecsa.nautanav.core.toTimeString
import cu.suitetecsa.nautanav.domain.model.UserModel
import cu.suitetecsa.nautanav.ui.components.CaptchaDialog
import cu.suitetecsa.nautanav.ui.components.CardChangeEmailPassword
import cu.suitetecsa.nautanav.ui.components.CardChangePassword
import cu.suitetecsa.nautanav.ui.components.CardNautaDetails
import cu.suitetecsa.nautanav.ui.components.CardNautaHomeDetails
import cu.suitetecsa.nautanav.ui.components.CardRecharge
import cu.suitetecsa.nautanav.ui.components.CardTransfer
import cu.suitetecsa.nautanav.ui.components.UserDetailsHead
import cu.suitetecsa.nautanav.ui.components.connectview.ConnectView
import cu.suitetecsa.nautanav.ui.components.connectview.ConnectViewState
import cu.suitetecsa.nautanav.ui.components.timepicker.TimePickerDialog

@Composable
fun CurrentUserDashboard(viewModel: NautaViewModel) {
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val context = LocalContext.current
    val currentUser: UserModel by viewModel.currentUser.collectAsState()

    // ViewStates
    val connectViewState: ConnectViewState by viewModel.connectViewState.collectAsState()

    // Update
    val captchaCode: String by viewModel.captchaCode.observeAsState(initial = "")
    val captchaImage: Bitmap? by viewModel.captchaImage.observeAsState(initial = null)
    val isLoadingCaptcha: Boolean by viewModel.isLoadingCaptcha.observeAsState(initial = false)
    val captchaLoadStatus: Pair<Boolean, String?> by viewModel.captchaLoadStatus.observeAsState(
        initial = Pair(true, null)
    )

    val isReadyToUpdate: Boolean by viewModel.isReadyToUpdate.observeAsState(initial = false)

    // Connect
    val leftTime: String by viewModel.leftTime.observeAsState(initial = currentUser.remainingTime.toTimeString())
    val limitedTime: String by viewModel.limitedTime.observeAsState(initial = leftTime)
    val connectStatus: Pair<Boolean, String?> by viewModel.connectStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val (isConnected, connectError) = connectStatus
    if (!isConnected) Toast.makeText(context, connectError, Toast.LENGTH_LONG).show()

    // User
    val isLogging: Boolean by viewModel.isLogging.observeAsState(initial = false)
    val loginStatus: Pair<Boolean, String?> by viewModel.loginStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val (isLogged, loginError) = loginStatus
    if (!isLogged) Toast.makeText(context, loginError, Toast.LENGTH_LONG).show()

    // Recharge
    val isRecharging: Boolean by viewModel.isRecharging.observeAsState(initial = false)
    val isEnabledRechargeButton: Boolean by viewModel.isEnabledRechargeButton.observeAsState(initial = false)
    val rechargeCode: String by viewModel.rechargeCode.observeAsState(initial = "")
    val rechargeStatus: Pair<Boolean, String?> by viewModel.rechargeStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val (isRecharged, rechargeError) = rechargeStatus
    if (!isRecharged) Toast.makeText(context, rechargeError, Toast.LENGTH_LONG).show()

    // Transfer
    val destinationAccount: TextFieldValue by viewModel.destinationAccount.observeAsState(
        initial = TextFieldValue(
            ""
        )
    )
    val amount: TextFieldValue by viewModel.amount.observeAsState(initial = TextFieldValue(""))
    val isEnabledTransferButton: Boolean by viewModel.isEnabledTransferButton.observeAsState(initial = false)
    val isTransferring: Boolean by viewModel.isTransferring.observeAsState(initial = false)
    val transferStatus: Pair<Boolean, String?> by viewModel.transferStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val (isTransferred, transferError) = transferStatus
    if (!isTransferred) Toast.makeText(context, transferError, Toast.LENGTH_LONG).show()

    // Change password
    val accessAccountNewPassword: String by viewModel.accessAccountNewPassword.observeAsState(
        initial = ""
    )
    val isChangingAccountAccessPassword: Boolean by viewModel.isChangingAccountAccessPassword.observeAsState(
        initial = false
    )
    val accessAccountChangePasswordStatus: Pair<Boolean, String?> by viewModel.accessAccountChangePasswordStatus.observeAsState(
        initial = Pair(true, null)
    )
    val (isAccessAccountPasswordChanged, accessAccountPasswordChangeError) = accessAccountChangePasswordStatus
    if (!isAccessAccountPasswordChanged) {
        Toast.makeText(
            context,
            accessAccountPasswordChangeError,
            Toast.LENGTH_LONG
        ).show()
    }

    // ChangeEmailPassword
    val emailOldPassword: String by viewModel.emailOldPassword.observeAsState(initial = "")
    val emailNewPassword: String by viewModel.emailNewPassword.observeAsState(initial = "")
    val isChangingEmailPassword: Boolean by viewModel.isChangingEmailPassword.observeAsState(initial = false)
    val emailChangePasswordStatus: Pair<Boolean, String?> by viewModel.emailChangePasswordStatus.observeAsState(
        initial = Pair(true, null)
    )
    val (isEmailPasswordChanged, emailPasswordChangeError) = emailChangePasswordStatus
    if (!isEmailPasswordChanged) {
        Toast.makeText(
            context,
            emailPasswordChangeError,
            Toast.LENGTH_LONG
        ).show()
    }

    // Dialog
    val showTimePickerDialog: Boolean by viewModel.showTimePickerDialog.observeAsState(initial = false)
    val showCaptchaDialog: Pair<Boolean, () -> Unit> by viewModel.showCaptchaDialog.observeAsState(
        initial = Pair(false) {}
    )
    val (isShowCaptchaDialog, postLoginAction) = showCaptchaDialog

    val (hour, minute, _) = limitedTime.split(":")
    TimePickerDialog(
        show = showTimePickerDialog,
        limitedTime = Pair(hour.toInt(), minute.toInt()),
        onDismiss = { viewModel.showTimePickerDialog(false) },
        onConfirm = {
            val (hours, minutes) = it
            val inSeconds = (hours * 3600) + (minutes * 60)
            if (inSeconds <= leftTime.toSeconds()) {
                viewModel.onChangeLimiterTime(it)
                viewModel.showTimePickerDialog(false)
            } else {
                Toast.makeText(context, "No dispones de tanto tiempo", Toast.LENGTH_LONG).show()
            }
        }
    )

    CaptchaDialog(
        show = isShowCaptchaDialog,
        captchaImage = captchaImage,
        captchaCode = captchaCode,
        isLoading = isLogging,
        isLoadingCaptcha = isLoadingCaptcha,
        isExecutable = isReadyToUpdate,
        captchaLoadStatus = captchaLoadStatus,
        onClickImage = { viewModel.getCaptcha() },
        onChangeCaptchaCode = { viewModel.onChangeCaptchaCode(it) },
        loginFunction = {
            viewModel.updateUser(it) {
                viewModel.showCaptchaDialog(false) {}
                postLoginAction()
            }
        },
        onDismiss = {
            viewModel.showCaptchaDialog(false) {}
        }
    )

    Column(modifier = Modifier.padding(bottom = 40.dp)) {
        UserDetailsHead(
            userName = currentUser.username,
            remainingTime = leftTime,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        ConnectView(
            modifier = Modifier.padding(vertical = 8.dp),
            connectViewState
        )
        CardNautaDetails(
            user = currentUser,
            isLoading = isLogging,
            loginStatus = loginStatus,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if (!currentUser.offer.isNullOrEmpty()) {
            CardNautaHomeDetails(
                user = currentUser,
                isLoading = isLogging,
                loginStatus = loginStatus,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = stringResource(id = R.string.recharge),
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            CardRecharge(
                isLoading = isRecharging,
                rechargeStatus = rechargeStatus,
                rechargeCode = rechargeCode,
                isExecutable = isEnabledRechargeButton,
                onChangeRechargeCode = { viewModel.onChangeRechargeCode(it) },
                onRecharge = {
                    if (!isLoading && rechargeCode.isValidRechargeCode()) viewModel.toUp(it)
                },
                onClickQRScannerIcon = {}
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = stringResource(id = R.string.transfer),
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            CardTransfer(
                destinationAccount = destinationAccount,
                amount = amount,
                isExecutable = isEnabledTransferButton,
                isLoading = isTransferring,
                transferStatus = transferStatus,
                onTransfer = { destinationAccount, amount ->
                    if (
                        viewModel.isValidDestinationAccount(destinationAccount) &&
                        amount.isValidAmountToTransfer(currentUser) &&
                        !isLoading
                    ) {
                        viewModel.transfer(amount.toFloat(), destinationAccount)
                    }
                },
                onChangeDestinationAccount = { viewModel.onTransferChange(it, amount) },
                onChangeAmount = { viewModel.onTransferChange(destinationAccount, it) }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = stringResource(id = R.string.change_password),
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            CardChangePassword(
                isLoading = isChangingAccountAccessPassword,
                newPassword = accessAccountNewPassword,
                changedPasswordStatus = accessAccountChangePasswordStatus,
                isExecutable = accessAccountNewPassword.isValidPassword(),
                onChangeNewPassword = { viewModel.onAccountAccessPasswordChange(it) },
                onChangePassword = { viewModel.changePassword(it) },
                onGeneratePassword = { viewModel.generatePassword(false) }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = stringResource(R.string.change_email_password),
                style = MaterialTheme.typography.headlineMedium.copy(color = MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            CardChangeEmailPassword(
                oldPassword = emailOldPassword,
                newPassword = emailNewPassword,
                onChangePassword = { oldPassword, newPassword ->
                    viewModel.onEmailPasswordChange(
                        oldPassword,
                        newPassword
                    )
                },
                onChangeEmailPassword = { oldPassword, newPassword ->
                    viewModel.changeEmailPassword(
                        oldPassword,
                        newPassword
                    )
                },
                onGeneratePassword = { viewModel.generatePassword(true) },
                isChangingEmailPassword = isChangingEmailPassword,
                changePasswordStatus = emailChangePasswordStatus
            )
        }
    }
}
