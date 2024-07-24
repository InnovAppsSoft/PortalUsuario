package com.marlon.portalusuario.presentation.mobileservices.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.ui.theme.VibrantTangerineOrange
import io.github.suitetecsa.sdk.android.utils.LongUtils.asRemainingDays
import io.github.suitetecsa.sdk.android.utils.StringUtils

@Composable
internal fun PlansSection(service: MobileService) {
    Column {
        if (service.plans.isNotEmpty()) {
            Text(
                text = "Planes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            service.plans.forEach {
                PlanCard(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    planTitle = it.type,
                    dataCount = it.data,
                    remainingDays = StringUtils.toDateMillis(it.expires.replace("/", "-")).asRemainingDays,
                    color = VibrantTangerineOrange
                )
            }
        }
        if (service.plans.isNotEmpty()) Spacer(modifier = Modifier.padding(4.dp))
    }
}
