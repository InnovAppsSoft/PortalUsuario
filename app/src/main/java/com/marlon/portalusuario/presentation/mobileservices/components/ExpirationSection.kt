package com.marlon.portalusuario.presentation.mobileservices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.ui.components.ArcProgressbar
import com.marlon.portalusuario.ui.theme.BrightCoralRed
import com.marlon.portalusuario.ui.theme.BrightOrange
import io.github.suitetecsa.sdk.android.utils.LongUtils.asRemainingDays
import io.github.suitetecsa.sdk.android.utils.StringUtils

private const val ElevenMonths = 330
private const val TwelveMonths = 360

@Composable
internal fun ExpirationSection(service: MobileService) {
    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ExpirationView(
            stringResource(id = R.string.sim_active_until),
            service.lockDate
        )
        ExpirationView(
            stringResource(id = R.string.expires),
            service.deletionDate
        )
    }
}

@Composable
fun ExpirationView(
    title: String,
    date: String
) {
    Row {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title)
            Text(text = date)
        }
    }
}
