package com.marlon.portalusuario.presentation.mobileservices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.domain.model.MobileService

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
            stringResource(id = R.string.elimination),
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
    Text(text = "$title: $date")
}
