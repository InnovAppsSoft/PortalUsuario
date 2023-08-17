package com.marlon.portalusuario.nauta.ui.components.connectview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme
import com.marlon.portalusuario.nauta.ui.components.PrettyCard
import com.marlon.portalusuario.nauta.ui.components.connectview.ConnectViewState.Connected
import com.marlon.portalusuario.nauta.ui.components.connectview.ConnectViewState.Connecting
import com.marlon.portalusuario.nauta.ui.components.connectview.ConnectViewState.Disconnected
import com.marlon.portalusuario.nauta.ui.components.connectview.ConnectViewState.FailConnectStatus

@Composable
fun ConnectView(
    modifier: Modifier = Modifier,
    state: ConnectViewState,
    remainingTime: String,
    isRunningSomeTask: Boolean,
    onLogin: () -> Unit,
    onForgotSession: () -> Unit,
    onRecoverSession: () -> Unit,
    onSelectLimitedTime: () -> Unit
) {
    val isEnabled = state !is Connecting || remainingTime != "00:00:00"
    PrettyCard(modifier = modifier, isLoading = state is Connecting) {
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
                    .clickable { if (!isRunningSomeTask && isEnabled) onSelectLimitedTime() },
                style = MaterialTheme.typography.h5
            )
            Button(
                onClick = if (state is FailConnectStatus) onRecoverSession else onLogin,
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    bottomStart = 8.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                modifier = Modifier.fillMaxHeight(),
                enabled = !isRunningSomeTask && isEnabled
            ) {
                Text(
                    text = when (state) {
                        Connected -> stringResource(R.string.disconnect)
                        Connecting -> "..."
                        Disconnected -> stringResource(R.string.connect)
                        is FailConnectStatus -> stringResource(R.string.reintent)
                    },
                    style = MaterialTheme.typography.button
                )
            }
            if (state is FailConnectStatus) {
                Spacer(modifier = Modifier.width(1.dp))
                Button(
                    onClick = onForgotSession,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        bottomStart = 0.dp,
                        topEnd = 8.dp,
                        bottomEnd = 8.dp
                    ),
                    modifier = Modifier.fillMaxHeight(),
                    enabled = isEnabled
                ) {
                    Text(
                        text = stringResource(R.string.forgot),
                        style = MaterialTheme.typography.button
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
        ConnectView(
            state = Disconnected,
            remainingTime = "00:00:00",
            isRunningSomeTask = false,
            onLogin = { },
            onRecoverSession = { },
            onForgotSession = { },
            modifier = Modifier.padding(16.dp)
        ) { }
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardConnectPreviewDark() {
    SuitEtecsaTheme {
        ConnectView(
            state = FailConnectStatus("errorop"),
            remainingTime = "00:00:00",
            isRunningSomeTask = false,
            onLogin = { },
            onRecoverSession = { },
            onForgotSession = { },
            modifier = Modifier.padding(16.dp)
        ) { }
    }
}
