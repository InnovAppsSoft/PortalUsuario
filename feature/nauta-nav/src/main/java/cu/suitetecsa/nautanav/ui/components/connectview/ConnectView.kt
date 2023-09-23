package cu.suitetecsa.nautanav.ui.components.connectview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cu.suitetecsa.nauta_nav.R
import cu.suitetecsa.nautanav.commons.ui.theme.SuitEtecsaTheme
import cu.suitetecsa.nautanav.ui.components.PrettyCard

@Composable
fun ConnectView(
    modifier: Modifier = Modifier,
    viewState: ConnectViewState
) {

    PrettyCard(modifier = modifier, isLoading = viewState is ConnectViewState.Loading) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Text(
                text = when (viewState) {
                    is ConnectViewState.Connected -> viewState.remainingTime
                    is ConnectViewState.Disconnected -> viewState.remainingTime
                    is ConnectViewState.Failure -> viewState.errorMessage
                    is ConnectViewState.Loading -> viewState.remainingTime
                },
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth()
                    .clickable(
                        enabled = viewState is ConnectViewState.Disconnected,
                        onClick = when (viewState) {
                            is ConnectViewState.Disconnected -> viewState.onSelectLimitedTime
                            else -> {
                                {}
                            }
                        }
                    ),
                style = MaterialTheme.typography.displayMedium
            )
            Button(
                onClick = when (viewState) {
                    is ConnectViewState.Connected -> viewState.onLogout
                    is ConnectViewState.Disconnected -> viewState.onLogin
                    is ConnectViewState.Failure -> viewState.onAction
                    is ConnectViewState.Loading -> {
                        {}
                    }
                },
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                modifier = Modifier.fillMaxHeight(),
                enabled = viewState !is ConnectViewState.Loading
            ) {
                Text(
                    text = when (viewState) {
                        is ConnectViewState.Connected -> stringResource(R.string.disconnect)
                        is ConnectViewState.Failure -> viewState.actionLabel
                        else -> stringResource(R.string.connect)
                    },
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardConnectPreview() {
    SuitEtecsaTheme {
        ConnectView(
            viewState = ConnectViewState.Disconnected("00:00:00", {}, {}),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CardConnectPreviewDark() {
    SuitEtecsaTheme {
        ConnectView(
            viewState = ConnectViewState.Disconnected("00:00:00", {}, {}),
            modifier = Modifier.padding(16.dp)
        )
    }
}
