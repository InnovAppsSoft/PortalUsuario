package cu.suitetecsa.nautanav.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cu.suitetecsa.nauta_nav.R
import cu.suitetecsa.nautanav.commons.ui.AnimatedPlaceholder
import cu.suitetecsa.nautanav.ui.components.CaptchaCanvas
import cu.suitetecsa.nautanav.ui.components.PasswordField
import cu.suitetecsa.nautanav.ui.components.PrettyCard
import cu.suitetecsa.nautanav.ui.components.UserField

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddUserDashboard(viewModel: NautaViewModel) {
    val userName: TextFieldValue by viewModel.userName.collectAsState()
    val password: String by viewModel.password.collectAsState()
    val captchaCode: String by viewModel.captchaCode.observeAsState(initial = "")
    val updateButtonIsEnabled: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val captchaImage: Bitmap? by viewModel.captchaImage.observeAsState(initial = null)
    val isLoadingCaptcha: Boolean by viewModel.isLoadingCaptcha.observeAsState(initial = false)
    val isLogging: Boolean by viewModel.isLogging.observeAsState(initial = false)
    val loginStatus: Pair<Boolean, String?> by viewModel.loginStatus.observeAsState(
        initial = Pair(
            true,
            null
        )
    )
    val captchaLoadStatus: Pair<Boolean, String?> by viewModel.captchaLoadStatus.observeAsState(
        initial = Pair(true, null)
    )

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    PrettyCard(isLoading = isLogging) {
        val (isOk, err) = loginStatus
        if (!isOk) Toast.makeText(context, err, Toast.LENGTH_LONG).show()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserField(userName) { viewModel.onLoginChanged(it, password, captchaCode) }
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordField(password) { viewModel.onLoginChanged(userName, it, captchaCode) }
            Spacer(modifier = Modifier.padding(4.dp))
            CaptchaCanvas(
                captchaImage = captchaImage,
                isLoading = isLoadingCaptcha,
                captchaLoadStatus = captchaLoadStatus
            ) { viewModel.getCaptcha() }
            Spacer(modifier = Modifier.padding(4.dp))
            TextField(
                value = captchaCode,
                onValueChange = { viewModel.onLoginChanged(userName, password, it) },
                placeholder = {
                    AnimatedPlaceholder(
                        hints = listOf(
                            stringResource(R.string.captcha_code),
                            "HL46Fr"
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (updateButtonIsEnabled) {
                            keyboardController?.hide()
                            viewModel.addUser(userName.text, password, captchaCode)
                        }
                    }
                ),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

