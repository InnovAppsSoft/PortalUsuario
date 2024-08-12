package com.marlon.portalusuario.presentation.mobileservices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.ui.theme.TropicalAquamarineGreen
import io.github.suitetecsa.sdk.android.utils.LongUtils.asRemainingDays
import io.github.suitetecsa.sdk.android.utils.StringUtils

@Composable
internal fun BonusSection(bonuses: List<MobileBonus>) {
    Row(modifier = Modifier.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Bonus",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.padding(4.dp))
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(bonuses) {
            PlanCard(
                modifier = Modifier.padding(horizontal = 4.dp),
                planTitle = it.type,
                dataCount = it.data,
                remainingDays = StringUtils.toDateMillis(it.expires.replace("/", "-")).asRemainingDays,
                color = TropicalAquamarineGreen
            )
        }
    }
    Spacer(modifier = Modifier.padding(4.dp))
}
