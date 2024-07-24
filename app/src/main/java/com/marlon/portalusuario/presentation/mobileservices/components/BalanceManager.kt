package com.marlon.portalusuario.presentation.mobileservices.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marlon.portalusuario.R
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.presentation.mobileservices.MobileServicesEvent
import com.marlon.portalusuario.ui.theme.VibrantGreen

@Composable
fun BalanceManager(
    modifier: Modifier = Modifier,
    onEvent: (MobileServicesEvent) -> Unit = {},
    canRun: Boolean = false,
    service: MobileService,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = stringResource(id = R.string.balance_credit),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = "$%s %s".format(service.mainBalance, service.currency),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 32.sp)
            )
            IconButton(
                onClick = { }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                    Divider(thickness = 5.dp, color = VibrantGreen)
                }
            }
            IconButton(
                onClick = { }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Send, contentDescription = null)
                    Divider(thickness = 5.dp, color = VibrantGreen)
                }
            }
        }
        ExpirationSection(service)

        Spacer(modifier = Modifier.padding(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.usage_based_pricing))
            Switch(
                checked = service.consumptionRate,
                onCheckedChange = { },
                enabled = canRun
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        PlansSection(service = service)
        BonusSection(service)
    }
}
