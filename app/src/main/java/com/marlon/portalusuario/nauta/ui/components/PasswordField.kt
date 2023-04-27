package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R

@Composable
fun PasswordField(password: String, onChangedText: (String) -> Unit) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    TextField(value = password,
        onValueChange = { onChangedText(it) },
        placeholder = { Text(text = stringResource(id = R.string.password_placeholder)) },
        modifier = Modifier
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
        ),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
            else ImageVector.vectorResource(id = R.drawable.ic_baseline_visibility_off_24)

            val description = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = description)
            }
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(8.dp)
    )
}