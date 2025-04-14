package com.marlon.portalusuario.presentation.mobileservices.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.data.asDateMillis
import com.marlon.portalusuario.data.asRemainingDays
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.ui.theme.TealBlue
import com.marlon.portalusuario.ui.theme.VibrantTangerineOrange

@Composable
internal fun PlansSection(plans: List<MobilePlan>, isSimPaired: Boolean = false,) {
    Column {
        Row(modifier = Modifier.padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Planes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
            if (isSimPaired) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .background(TealBlue, shape = MaterialTheme.shapes.small)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add plan",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(plans) {
                PlanCard(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    planTitle = it.type,
                    dataCount = it.data,
                    remainingDays = it.expires.asDateMillis?.asRemainingDays,
                    color = VibrantTangerineOrange
                )
            }
        }
        if (plans.isNotEmpty()) Spacer(modifier = Modifier.padding(4.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PlansSectionPreview() {
    PortalUsuarioTheme {
        Surface(modifier = Modifier.padding(vertical = 16.dp)) {
            PlansSection(
                plans = listOf(
                    MobilePlan("01:23:55", "MINUTOS", "30/04/2025"),
                    MobilePlan("1560", "SMS", "30/04/2025"),
                    MobilePlan("719.55 MB", "DATOS", "30/04/2025"),
                    MobilePlan("0.00 B", "DATOS LTE", "30/04/2025"),
                )
            )
        }
    }
}
