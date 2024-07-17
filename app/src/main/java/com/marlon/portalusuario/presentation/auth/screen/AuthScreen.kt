package com.marlon.portalusuario.presentation.auth.screen

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.marlon.portalusuario.BuildConfig
import com.marlon.portalusuario.R
import com.marlon.portalusuario.activities.MainActivity
import com.marlon.portalusuario.presentation.auth.AuthEvent
import com.marlon.portalusuario.presentation.auth.AuthEvent.OnChangePassword
import com.marlon.portalusuario.presentation.auth.AuthEvent.OnLoadCaptcha
import com.marlon.portalusuario.presentation.auth.AuthViewModel
import com.marlon.portalusuario.ui.components.CaptchaCanvas
import com.marlon.portalusuario.ui.components.CaptchaField
import com.marlon.portalusuario.ui.components.NautaUserField
import com.marlon.portalusuario.ui.components.PasswordField
import cu.suitetecsa.nautanav.ui.components.PrettyCard

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel(), navController: NavHostController) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) { viewModel.onEvent(OnLoadCaptcha) }

    LaunchedEffect(key1 = viewModel.isLoggedIn.value) {
        if (viewModel.isLoggedIn.value) context.startActivity(Intent(context, MainActivity::class.java))
    }
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Column {
            PrettyCard(isLoading = viewModel.state.value.isLoading) {
                Column {
                    NautaUserField(
                        user = viewModel.state.value.phoneNumber,
                        onChangedText = { viewModel.onEvent(AuthEvent.OnChangePhoneNumber(it)) }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    PasswordField(
                        password = viewModel.state.value.password,
                        isPasswordVisible = viewModel.state.value.isPasswordVisible,
                        onChangedText = { viewModel.onEvent(OnChangePassword(it)) },
                        onChangePasswordVisibility = { viewModel.onEvent(AuthEvent.OnTogglePasswordVisibility) }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    CaptchaCanvas(
                        captchaImage = viewModel.state.value.captchaImage,
                        isLoading = viewModel.state.value.isLoadingCaptcha,
                        error = viewModel.state.value.error,
                        onReload = { viewModel.onEvent(OnLoadCaptcha) }
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    CaptchaField(
                        value = viewModel.state.value.captchaCode,
                        onChangedValue = { viewModel.onEvent(AuthEvent.OnChangeCaptchaCode(it)) },
                        onDone = { viewModel.onEvent(AuthEvent.OnAuth) }
                    )
                }
            }
            RestorePasswordAndCreateAccount(navController)

            if (BuildConfig.DEBUG) {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { context.startActivity(Intent(context, MainActivity::class.java)) }
                ) {
                    Text(text = "Saltar")
                }
            }
        }
    }
}

@Composable
private fun RestorePasswordAndCreateAccount(navController: NavHostController) {
    Spacer(modifier = Modifier.height(32.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        Text(text = stringResource(R.string.forgot_password))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.clickable { navController.navigate("reset_password") },
            text = stringResource(R.string.reset_password),
            textDecoration = TextDecoration.Underline
        )
    }
    Spacer(modifier = Modifier.padding(4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        Text(text = stringResource(R.string.not_have_account))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.clickable { navController.navigate("signup") },
            text = stringResource(R.string.create_an_account),
            textDecoration = TextDecoration.Underline
        )
    }
}
