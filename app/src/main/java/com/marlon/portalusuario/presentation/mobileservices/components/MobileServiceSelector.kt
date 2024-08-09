package com.marlon.portalusuario.presentation.mobileservices.components

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.marlon.portalusuario.domain.model.MobileBonus
import com.marlon.portalusuario.domain.model.MobilePlan
import com.marlon.portalusuario.domain.model.MobileService
import com.marlon.portalusuario.domain.model.ServiceType
import com.marlon.portalusuario.ui.theme.BrightCoralRed
import com.marlon.portalusuario.ui.theme.PortalUsuarioTheme
import com.marlon.portalusuario.ui.theme.TealBlue
import com.marlon.portalusuario.util.Utils.fixDateFormat
import cu.suitetecsa.nautanav.ui.components.PrettyCard
import cu.suitetecsa.nautanav.ui.components.Spinner
import io.github.suitetecsa.sdk.android.model.SimCard

@Composable
fun MobileServiceSelector(
    services: List<MobileService>,
    serviceSelected: MobileService,
    onServiceSelected: (MobileService) -> Unit,
    simCards: List<SimCard> = listOf(),
    onShowServiceSettings: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PrettyCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spinner(
                items = services,
                selectedItem = serviceSelected,
                onItemSelected = onServiceSelected,
                selectedItemFactory = { mod, item -> ServiceItem(item, simCards, mod) },
                dropdownItemFactory = { item, _ -> ServiceItem(item, simCards) }
            )
        }

        PrettyCard(modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = onShowServiceSettings) {
                Icon(imageVector = Icons.Outlined.Settings, contentDescription = null)
            }
        }
    }
}

@SuppressLint("MissingPermission", "HardwareIds")
@Composable
private fun ServiceItem(
    item: MobileService,
    simCards: List<SimCard>,
    modifier: Modifier = Modifier
) {
    val simCard = simCards.firstOrNull { it.slotIndex == item.slotIndex }
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.background(simCard?.let { TealBlue } ?: BrightCoralRed)) {
            Text(
                text = simCard?.let { sim ->
                    sim.displayName?.let { "$it (SIM ${sim.slotIndex + 1})" }
                        ?: "SIM ${sim.slotIndex + 1}"
                } ?: "No SIM",
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = item.phoneNumber,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun MobileServiceSelectorPreview() {
    val services = listOf(
        MobileService(
            id = "5351872843",
            lte = false,
            advanceBalance = "0",
            status = "Activo",
            lockDate = "26/6/2024".fixDateFormat(),
            deletionDate = "26/7/2024".fixDateFormat(),
            saleDate = "19/8/2022".fixDateFormat(),
            internet = true,
            plans = listOf(
                MobilePlan("01:23:55", "MINUTOS", "31/08/2023".replace("/", "-")),
                MobilePlan("1560", "SMS", "31/08/2023".replace("/", "-")),
                MobilePlan("719.55 MB", "DATOS", "31/08/2023".replace("/", "-")),
                MobilePlan("0.00 B", "DATOS LTE", "31/08/2023".replace("/", "-")),
            ),
            bonuses = listOf(
                MobileBonus("296.61 MB", "", "DATOS NACIONALES", "31/08/2023".replace("/", "-")),
                MobileBonus(
                    "0",
                    "18/03/2023".replace("/", "-"),
                    "DATOS LTE",
                    "31/08/2023".replace("/", "-")
                ),
            ),
            currency = "CUP",
            phoneNumber = "51872843",
            mainBalance = "10.00",
            consumptionRate = true,
            slotIndex = -1,
            type = ServiceType.Local
        )
    )
    PortalUsuarioTheme {
        Surface {
            MobileServiceSelector(
                services = services,
                serviceSelected = services.first { it.id == "5351872843" },
                onServiceSelected = {}
            )
        }
    }
}
