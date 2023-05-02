package com.marlon.portalusuario.nauta.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.codescanner.ui.components.QRScannerDialog
import com.marlon.portalusuario.nauta.core.INITIAL_USER
import com.marlon.portalusuario.nauta.core.isValidAmountToTransfer
import com.marlon.portalusuario.nauta.core.isValidPassword
import com.marlon.portalusuario.nauta.core.isValidRechargeCode
import com.marlon.portalusuario.nauta.core.toSeconds
import com.marlon.portalusuario.nauta.core.toTimeString
import com.marlon.portalusuario.nauta.domain.model.UserModel
import com.marlon.portalusuario.nauta.ui.components.CaptchaDialog
import com.marlon.portalusuario.nauta.ui.components.CardChangeEmailPassword
import com.marlon.portalusuario.nauta.ui.components.CardChangePassword
import com.marlon.portalusuario.nauta.ui.components.CardConnect
import com.marlon.portalusuario.nauta.ui.components.CardNautaDetails
import com.marlon.portalusuario.nauta.ui.components.CardNautaHomeDetails
import com.marlon.portalusuario.nauta.ui.components.CardRecharge
import com.marlon.portalusuario.nauta.ui.components.CardTransfer
import com.marlon.portalusuario.nauta.ui.components.UserDetailsHead
import com.marlon.portalusuario.nauta.ui.components.timepicker.TimePickerDialog

@Composable
fun CurrentUserDashboard(viewModel: NautaViewModel) {
    val isLoading: Boolean by viewModel.isLoading.observeAsState(initial = false)
    val context = LocalContext.current
    val currentUser: UserModel by viewModel.currentUser.observeAsState(initial = INITIAL_USER)

    //Update
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
    val isConnecting: Boolean by viewModel.isConnecting.observeAsState(initial = false)
    val connectStatus: Pair<Boolean, String?> by viewModel.connectStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val isLoggedIn: Boolean by viewModel.isLoggedIn.observeAsState(initial = false)

    // User
    val isLogging: Boolean by viewModel.isLogging.observeAsState(initial = false)
    val loginStatus: Pair<Boolean, String?> by viewModel.loginStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )

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

    // ChangeEmailPassword
    val emailOldPassword: String by viewModel.emailOldPassword.observeAsState(initial = "")
    val emailNewPassword: String by viewModel.emailNewPassword.observeAsState(initial = "")
    val isChangingEmailPassword: Boolean by viewModel.isChangingEmailPassword.observeAsState(initial = false)
    val emailChangePasswordStatus: Pair<Boolean, String?> by viewModel.emailChangePasswordStatus.observeAsState(
        initial = Pair(true, null)
    )

    // Dialog
    val showTimePickerDialog: Boolean by viewModel.showTimePickerDialog.observeAsState(initial = false)
    val showCaptchaDialog: Pair<Boolean, () -> Unit> by viewModel.showCaptchaDialog.observeAsState(
        initial = Pair(false) {})
    val (isShowCaptchaDialog, postLoginAction) = showCaptchaDialog
    val showQRCodeDialog: Pair<Boolean, (String) -> Unit> by viewModel.showQRCodeScanner.observeAsState(
        initial = Pair(false) {})
    val (isShowQRCodeDialog, onQRCodeRead) = showQRCodeDialog


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
        })

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

    QRScannerDialog(
        show = isShowQRCodeDialog,
        onQRCodeRead = {
            onQRCodeRead(it)
            viewModel.showQRCodeScanner(false) {}
        },
        onDismiss = {
            viewModel.showQRCodeScanner(false) {}
        }
    )

    // Showing errors
    val (isRechargeOk, rechargeErrors) = rechargeStatus
    if (!isRechargeOk) {
        Toast.makeText(context, rechargeErrors, Toast.LENGTH_LONG).show()
        viewModel.resetStatus()
    }

    val (isTransferOk, transferErrors) = transferStatus
    if (!isTransferOk) {
        Toast.makeText(context, transferErrors, Toast.LENGTH_LONG).show()
        viewModel.resetStatus()
    }

    Column(modifier = Modifier.padding(bottom = 40.dp)) {

        UserDetailsHead(
            userName = currentUser.username,
            remainingTime = leftTime,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        CardConnect(
            remainingTime = limitedTime,
            connectButtonEnabled = currentUser.remainingTime != 0 && !isLogging,
            isLoading = isConnecting,
            connectStatus = connectStatus,
            isLoggedIn = isLoggedIn,
            modifier = Modifier.padding(vertical = 8.dp),
            onLogin = {
                if (!isLoggedIn) viewModel.connect(currentUser.username, currentUser.password)
                else viewModel.disconnect()
            }) { viewModel.showTimePickerDialog(true) }
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
                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground)
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
                onClickQRScannerIcon = {
                    viewModel.showQRCodeScanner(true) {
                        viewModel.onChangeRechargeCode(it)
                    }
                }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(
                text = stringResource(id = R.string.transfer),
                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground)
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
                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground)
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
                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground)
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