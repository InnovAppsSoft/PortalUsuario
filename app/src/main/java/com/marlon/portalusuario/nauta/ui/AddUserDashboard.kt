package com.marlon.portalusuario.nauta.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.AnimatedPlaceholder
import com.marlon.portalusuario.nauta.ui.components.CaptchaCanvas
import com.marlon.portalusuario.nauta.ui.components.PasswordField
import com.marlon.portalusuario.nauta.ui.components.PrettyCard
import com.marlon.portalusuario.nauta.ui.components.UserField

@Composable
fun AddUserDashboard(viewModel: NautaViewModel) {
    val userName: TextFieldValue by viewModel.userName.observeAsState(initial = TextFieldValue(text = ""))
    val password: String by viewModel.password.observeAsState(initial = "")
    val captchaCode: String by viewModel.captchaCode.observeAsState(initial = "")
    val updateButtonIsEnabled: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val captchaImage: Bitmap? by viewModel.captchaImage.observeAsState(initial = null)
    val isLoadingCaptcha: Boolean by viewModel.isLoadingCaptcha.observeAsState(initial = false)
    val captchaLoadStatus: Pair<Boolean, String?> by viewModel.captchaLoadStatus.observeAsState(
        initial = Pair(true, null)
    )

    val context = LocalContext.current

    PrettyCard {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(8.dp))
            UserField(userName) { viewModel.onLoginChanged(it, password, captchaCode) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChanged(userName, it, captchaCode) }
            Spacer(modifier = Modifier.padding(8.dp))
            CaptchaCanvas(
                captchaImage = captchaImage,
                isLoading = isLoadingCaptcha,
                captchaLoadStatus = captchaLoadStatus
            ) { viewModel.getCaptcha() }
            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                TextField(
                    value = captchaCode, onValueChange = { viewModel.onLoginChanged(userName, password, it) },
                    placeholder = {
                        AnimatedPlaceholder(
                            hints = listOf(
                                stringResource(R.string.captcha_code),
                                "HL46Fr"
                            )
                        )
                    },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (updateButtonIsEnabled) {
                                viewModel.login(userName.text, password, captchaCode)
                            }
                        }
                    ),
                    singleLine = true,
                    maxLines = 1,
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
                Button(
                    onClick = { viewModel.login(userName.text, password, captchaCode) },
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                    modifier = Modifier.fillMaxHeight(),
                    enabled = updateButtonIsEnabled
                ) {
                    Text(text = stringResource(R.string.update))
                }
            }
        }
    }
}