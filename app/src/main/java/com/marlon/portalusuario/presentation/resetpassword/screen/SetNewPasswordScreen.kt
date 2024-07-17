package com.marlon.portalusuario.presentation.resetpassword.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.ui.components.PasswordField
import cu.suitetecsa.nautanav.ui.components.PrettyCard

@Composable
fun SetNewPasswordScreen(
    password: String = "",
    confirmPassword: String = "",
    isLoading: Boolean = false,
    isPasswordVisible: Boolean = false,
    onChangedPassword: (String) -> Unit = {},
    onChangedConfirmPassword: (String) -> Unit = {},
    onTogglePasswordVisibility: () -> Unit = {},
    onSetPassword: () -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                text = stringResource(R.string.reset_password_message),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrettyCard(isLoading = isLoading) {
                Column {
                    PasswordField(
                        password,
                        isPasswordVisible,
                        onChangedPassword,
                        onTogglePasswordVisibility
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PasswordField(
                        confirmPassword,
                        isPasswordVisible,
                        onChangedConfirmPassword,
                        onTogglePasswordVisibility,
                        ImeAction.Done,
                        onSetPassword
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun SetNewPasswordScreenPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SetNewPasswordScreen()
        }
    }
}
