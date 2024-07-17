package com.marlon.portalusuario.presentation.signup.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.presentation.signup.SignupEvent
import com.marlon.portalusuario.presentation.signup.SignupEvent.OnChangedDNI
import com.marlon.portalusuario.presentation.signup.SignupEvent.OnChangedPhoneNumber
import com.marlon.portalusuario.presentation.signup.SignupEvent.OnLoadCaptcha
import com.marlon.portalusuario.presentation.signup.SignupState
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.ui.components.CaptchaCanvas
import com.marlon.portalusuario.ui.components.CaptchaField
import com.marlon.portalusuario.ui.components.NautaUserField
import cu.suitetecsa.nautanav.commons.ui.AnimatedPlaceholder
import cu.suitetecsa.nautanav.ui.components.PrettyCard

@Composable
fun RegisterUserScreen(state: SignupState, onEvent: (SignupEvent) -> Unit) {
    LaunchedEffect(key1 = true) { onEvent(OnLoadCaptcha) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PrettyCard(modifier = Modifier.padding(16.dp), isLoading = state.isLoading) {
            Column {
                NautaUserField(state.phoneNumber, !state.isLoading) { onEvent(OnChangedPhoneNumber(it)) }
                Spacer(modifier = Modifier.height(4.dp))
                DniField(state.dni, !state.isLoading) { onEvent(OnChangedDNI(it)) }
                Spacer(modifier = Modifier.height(4.dp))
                CaptchaCanvas(
                    captchaImage = state.captchaImage,
                    isLoading = state.isLoadingCaptcha,
                    error = state.error
                ) { onEvent(OnLoadCaptcha) }
                Spacer(modifier = Modifier.height(4.dp))
                CaptchaField(
                    value = state.captchaCode,
                    onChangedValue = { onEvent(SignupEvent.OnChangedCaptchaCode(it)) }
                ) { onEvent(SignupEvent.OnCreateUser) }
            }
        }
    }
}

@Composable
fun DniField(value: String = "", enabled: Boolean = false, onChangedValue: (String) -> Unit = {}) {
    TextField(
        value = value,
        onValueChange = onChangedValue,
        placeholder = { AnimatedPlaceholder(hints = listOf(stringResource(R.string.dni), "49082596321")) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp),
        enabled = enabled
    )
}

@Preview
@Composable
private fun RegisterUserPreview() {
    PortalUsuarioTheme {
        Surface {
            RegisterUserScreen(state = SignupState()) { }
        }
    }
}
