package com.marlon.portalusuario.presentation.resetpassword.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.presentation.resetpassword.ResetPasswordEvent
import com.marlon.portalusuario.presentation.resetpassword.ResetPasswordState
import com.marlon.portalusuario.ui.components.CaptchaCanvas
import com.marlon.portalusuario.ui.components.CaptchaField
import com.marlon.portalusuario.ui.components.NautaUserField
import com.marlon.portalusuario.ui.components.PrettyCard
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme

@Composable
fun ValidateUserScreen(
    state: ResetPasswordState = ResetPasswordState(),
    onEvent: (ResetPasswordEvent) -> Unit = {}
) {
    LaunchedEffect(key1 = true) { onEvent(ResetPasswordEvent.OnLoadCaptcha) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        PrettyCard(modifier = Modifier.padding(16.dp), isLoading = state.isLoading) {
            Column {
                NautaUserField(
                    state.phoneNumber,
                    !state.isLoading
                ) { onEvent(ResetPasswordEvent.OnChangeUser(it)) }
                Spacer(modifier = Modifier.height(4.dp))
                CaptchaCanvas(
                    captchaImage = state.captchaImage,
                    isLoading = state.isLoadingCaptcha,
                    error = state.error
                ) { onEvent(ResetPasswordEvent.OnLoadCaptcha) }
                Spacer(modifier = Modifier.height(4.dp))
                CaptchaField(
                    value = state.captchaCode,
                    onChangedValue = { onEvent(ResetPasswordEvent.OnChangeCaptchaCode(it)) },
                    enabled = !state.isLoading && !state.isLoadingCaptcha && state.captchaImage != null
                ) { onEvent(ResetPasswordEvent.OnValidateUser) }
            }
        }
    }
}

@Preview
@Composable
private fun ValidateUserScreenPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) { ValidateUserScreen() }
    }
}
