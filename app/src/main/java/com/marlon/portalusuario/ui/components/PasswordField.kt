package com.marlon.portalusuario.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme

@Composable
fun PasswordField(
    password: String = "",
    isPasswordVisible: Boolean = false,
    onChangedText: (String) -> Unit = {},
    onChangePasswordVisibility: () -> Unit = {},
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = password,
        onValueChange = onChangedText,
        placeholder = { Text(text = stringResource(R.string.my_password_hint)) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            onDone()
        }),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "") },
        trailingIcon = {
            val icon =
                if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
            val description =
                if (isPasswordVisible) {
                    stringResource(id = R.string.hide_password)
                } else {
                    stringResource(
                        id = R.string.show_password
                    )
                }
            IconButton(onClick = onChangePasswordVisibility) {
                Icon(imageVector = icon, contentDescription = description)
            }
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.small
    )
}

@Preview
@Composable
private fun PasswordFieldPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            PrettyCard { PasswordField() }
        }
    }
}
