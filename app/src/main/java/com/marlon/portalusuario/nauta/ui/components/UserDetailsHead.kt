package com.marlon.portalusuario.nauta.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.marlon.portalusuario.commons.ui.theme.SuitEtecsaTheme

@Composable
fun UserDetailsHead(modifier: Modifier = Modifier, userName: String, remainingTime: String) {
    Box(modifier = modifier) {
        Column {
            Text(
                text = remainingTime,
                style = MaterialTheme.typography.h4.copy(color = MaterialTheme.colors.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.subtitle1.copy(color = MaterialTheme.colors.onBackground),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserDetailsHeadPreview() {
    SuitEtecsaTheme {
        UserDetailsHead(
            userName = "user.name@nauta.com.cu", remainingTime = "05:26:49"
        )
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun UserDetailsHeadPreviewDark() {
    SuitEtecsaTheme {
        UserDetailsHead(
            userName = "user.name@nauta.com.cu", remainingTime = "05:26:49"
        )
    }
}