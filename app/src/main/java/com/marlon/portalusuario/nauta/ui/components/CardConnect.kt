package com.marlon.portalusuario.nauta.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun CardConnect(
    modifier: Modifier = Modifier,
    remainingTime: String,
    connectButtonEnabled: Boolean,
    isLoading: Boolean,
    connectStatus: Pair<Boolean, String?>,
    isLoggedIn: Boolean,
    onLogin: () -> Unit,
    onSelectLimitedTime: () -> Unit
) {
    val (isOk, errors) = connectStatus
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
                    .clickable { onSelectLimitedTime() },
                style = MaterialTheme.typography.h5
            )
            Button(
                onClick = { onLogin() },
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier.fillMaxHeight(),
                enabled = connectButtonEnabled
            ) {
                if (!isLoggedIn) {
                    Text(text = stringResource(R.string.connect))
                } else {
                    Text(text = stringResource(R.string.disconnect))
                }
            }
        }
    }
}

@Preview
@Composable
fun CardConnectPreview() {
    CardConnect(
        remainingTime = "",
        connectButtonEnabled = false,
        isLoading = false,
        connectStatus = Pair(true, null),
        isLoggedIn = false,
        onLogin = { /*TODO*/ }) {

    }
}