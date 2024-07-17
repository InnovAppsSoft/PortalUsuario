package com.marlon.portalusuario.presentation.resetpassword.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme

private const val ConfirmCodeLength = 5

@Composable
fun ConfirmCodeScreen(
    code: String = "",
    enabled: Boolean = false,
    onChangedCode: (String) -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = code) {
        if (code.length == ConfirmCodeLength) {
            keyboardController?.hide()
            onConfirm()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
                text = stringResource(R.string.confirm_code_message),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(32.dp))
            BasicTextField(
                value = code,
                onValueChange = onChangedCode,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Characters
                ),
                decorationBox = {
                    Row(horizontalArrangement = Arrangement.Center) {
                        repeat(ConfirmCodeLength) { index ->
                            val char = when {
                                index >= code.length -> ""
                                else -> code[index].toString()
                            }
                            val isFocused = code.length == index
                            Text(
                                modifier = Modifier
                                    .width(40.dp)
                                    .border(
                                        if (isFocused) 2.dp else 1.dp,
                                        if (isFocused) Color.DarkGray else Color.LightGray,
                                        MaterialTheme.shapes.small
                                    ),
                                text = char,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                },
                enabled = enabled
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmCodeScreenPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ConfirmCodeScreen()
        }
    }
}
