package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme

@Composable
fun CardConnect(
    modifier: Modifier = Modifier,
    remainingTime: String,
    connectButtonEnabled: Boolean,
    isLoading: Boolean,
    isButtonEnabled: Boolean = true,
    connectStatus: Pair<Boolean, String?>,
    isLoggedIn: Boolean,
    onLogin: () -> Unit,
    onSelectLimitedTime: () -> Unit
) {
    val (isOk, errors) = connectStatus
    val context = LocalContext.current
    if (!isOk) Toast.makeText(context, errors, Toast.LENGTH_LONG).show()
    val isEnabled = isButtonEnabled && !isLoading
    PrettyCard(modifier = modifier, isLoading = isLoading, isFoundErrors = !isOk) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = remainingTime,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .clickable { if (isEnabled) { onSelectLimitedTime() } },
                style = MaterialTheme.typography.displayMedium
            )
            Button(
                onClick = { onLogin() },
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier.fillMaxHeight(),
                enabled = connectButtonEnabled && isEnabled
            ) {
                if (!isLoggedIn) {
                    Text(
                        text = stringResource(R.string.connect),
                        style = MaterialTheme.typography.labelMedium
                    )
                } else {
                    Text(
                        text = stringResource(R.string.disconnect),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardConnectPreview() {
    SuitEtecsaTheme {
        CardConnect(
            remainingTime = "00:00:00",
            connectButtonEnabled = false,
            isLoading = false,
            connectStatus = Pair(true, null),
            isLoggedIn = false,
            onLogin = { /*TODO*/ },
            modifier = Modifier.padding(16.dp)
        ) {

        }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardConnectPreviewDark() {
    SuitEtecsaTheme {
        CardConnect(
            remainingTime = "00:00:00",
            connectButtonEnabled = false,
            isLoading = false,
            connectStatus = Pair(true, null),
            isLoggedIn = false,
            onLogin = { /*TODO*/ },
            modifier = Modifier.padding(16.dp)
        ) {

        }
    }
}