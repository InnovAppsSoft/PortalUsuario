package com.marlon.portalusuario.nauta.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
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
import com.marlon.portalusuario.commons.NavigationType
import com.marlon.portalusuario.commons.fullUserName
import com.marlon.portalusuario.nauta.data.entities.User
import com.marlon.portalusuario.nauta.ui.components.CardConnect
import com.marlon.portalusuario.nauta.ui.components.CardNautaDetails
import com.marlon.portalusuario.nauta.ui.components.CardNautaHomeDetails
import com.marlon.portalusuario.nauta.ui.components.CardRecharge
import com.marlon.portalusuario.nauta.ui.components.CardTransfer
import com.marlon.portalusuario.nauta.ui.components.UserDetailsHead
import com.marlon.portalusuario.nauta.ui.components.timepicker.TimePickerDialog
import cu.suitetecsa.sdk.nauta.domain.util.timeStringToSeconds

@Composable
fun CurrentUserDashboard(viewModel: NautaViewModel) {
    val context = LocalContext.current
    val currentUser: User by viewModel.currentUser.observeAsState(
        initial = User(
            0,
            "",
            "",
            NavigationType.INTERNATIONAL,
            0L,
            ", ",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    )

    // Connect
    val leftTime: String by viewModel.leftTime.observeAsState(initial = currentUser.time)
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
    val rechargeCode: TextFieldValue by viewModel.rechargeCode.observeAsState(
        initial = TextFieldValue(
            ""
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
    val rechargeStatus: Pair<Boolean, String?> by viewModel.rechargeStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val transferStatus: Pair<Boolean, String?> by viewModel.transferStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )

    // Dialog
    val showTimePickerDialog: Boolean by viewModel.showTimePickerDialog.observeAsState(initial = false)
    val showCaptchaDialog: Boolean by viewModel.showCaptchaDialog.observeAsState(initial = false)

    if (showTimePickerDialog) {
        val (hour, minute, _) = limitedTime.split(":")
        TimePickerDialog(
            limitedTime = Pair(hour.toInt(), minute.toInt()),
            onDismiss = { viewModel.showTimePickerDialog(false) },
            onConfirm = {
                val (hours, minutes) = it
                val inSeconds = (hours * 3600) + (minutes * 60)
                if (inSeconds <= timeStringToSeconds(leftTime)) {
                    viewModel.onChangeLimiterTime(it)
                    viewModel.showTimePickerDialog(false)
                } else {
                    Toast.makeText(context, "No dispones de tanto tiempo", Toast.LENGTH_LONG).show()
                }
            })
    }

    Column {

        UserDetailsHead(
            userName = currentUser.fullUserName(),
            remainingTime = leftTime,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        CardConnect(
            remainingTime = limitedTime,
            connectButtonEnabled = currentUser.time != "00:00:00" && !isLogging,
            isLoading = isConnecting,
            connectStatus = connectStatus,
            isLoggedIn = isLoggedIn,
            modifier = Modifier.padding(16.dp),
            onLogin = {
                if (isLoggedIn) {
                    viewModel.connect(currentUser.fullUserName(), currentUser.password)
                } else {
                    viewModel.disconnect()
                }
            }) { viewModel.showTimePickerDialog(true) }
        CardNautaDetails(
            user = currentUser,
            isLoading = isLogging,
            loginStatus = loginStatus,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        if (!currentUser.offer.isNullOrEmpty()) {
            CardNautaHomeDetails(
                user = currentUser,
                isLoading = isLogging,
                loginStatus = loginStatus,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.padding(8.dp))
        }
        Column {
            Text(
                text = stringResource(id = R.string.recharge),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            CardRecharge(
                isLoading = isRecharging,
                rechargeStatus = rechargeStatus,
                rechargeCode = rechargeCode,
                isRechargeButtonEnabled = isEnabledRechargeButton,
                onChangeRechargeCode = { viewModel.onChangeRechargeCode(it) },
                onRecharge = { viewModel.toUp(it.text) }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Column {
            Text(text = stringResource(id = R.string.transfer))
            Spacer(modifier = Modifier.padding(4.dp))
            CardTransfer(
                destinationAccount = destinationAccount,
                amount = amount,
                transferButtonEnabled = isEnabledTransferButton,
                isLoading = isTransferring,
                transferStatus = transferStatus,
                onTransfer = { destinationAccount, amount ->
                    viewModel.transfer(amount.toFloat(), destinationAccount)
                },
                onChangeDestinationAccount = { viewModel.onTransferChange(it, amount) },
                onChangeAmount = { viewModel.onTransferChange(destinationAccount, it) }
            )
        }
    }
}